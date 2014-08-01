# encoding: utf-8
require "logstash/filters/base"
require "logstash/namespace"

# The clone filter is for duplicating events.
# A clone will be made for each type in the clone list.
# The original event is left unchanged.
class LogStash::Filters::SodaClone < LogStash::Filters::Base

  #Usage:
  #  filter{
  #    sodaclone{  
  #       clones => ["type1", "type2", ...]
  #    }
  #  }
  config_name "sodaclone"
  milestone 2

  # A new clone will be created with the given type for each type in this list.
  config :clones, :validate => :array, :default => []

  public
  def register
    # Nothing to do
  end

  public
  def filter(event)
    return unless filter?(event)
    @clones.each do |type|
      # cloen event
      clone = event.clone

      # add "type" and "value" field to the cloned event
      clone["type"] = type
      clone["value"] = clone[type]
      
      # remove all the old fields from cloned event
      @clones.each do |field|
        clone.remove(field)
      end
      clone.remove("@version")

      # filter_matched mark the successful code of plugin
      filter_matched(clone)
      @logger.debug("Cloned event", :clone => clone, :event => event)
      # Push this new event onto the stack at the LogStash::FilterWorker
      yield clone
    end
  end

end # class LogStash::Filters::Clone
