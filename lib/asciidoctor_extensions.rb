=begin

Copyright (C) 2014 Robert Winslow Dalpe

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

=end

require 'asciidoctor/extensions' unless RUBY_ENGINE == 'opal'

include ::Asciidoctor

class GlossTermInlineMacro < Extensions::InlineMacroProcessor
  use_dsl

  named :glossterm
  name_positional_attributes 'baseform', 'termtext', 'noindex'

  def process parent, target, attrs
    baseform = (attrs.key? 'baseform') ? attrs['baseform'] : nil
    termtext = (attrs.key? 'termtext') ? attrs['termtext'] : baseform
    noindex = (attrs.key? 'noindex') ? attrs['noindex'] == '1' : false
    baseformAttr = baseform != nil ? %(baseform="#{baseform}") : ""
    termtext = termtext != nil ? termtext : ""
    if noindex
      %(<glossterm #{baseformAttr}>#{termtext}</glossterm>)
    else
      %(<glossterm #{baseformAttr}>#{termtext}</glossterm><indexterm><primary>#{termtext}</primary></indexterm>)
    end
  end
end

class OlinkInlineMacro < Extensions::InlineMacroProcessor
  use_dsl

  named :olink
  name_positional_attributes 'targetptr', 'text'
  
  def process parent, target, attrs
    if attrs.key? 'text'
      %(<olink targetdoc="#{target}" targetptr="#{attrs['targetptr']}">#{attrs['text']}</olink>)
    else
      %(<olink targetdoc="#{target}" targetptr="#{attrs['targetptr']}"/>)
    end
  end
end

class CiteTitleInlineMacro < Extensions::InlineMacroProcessor
  use_dsl

  named :citetitle
  name_positional_attributes 'text'
  
  def process parent, target, attrs
    %(<citetitle>#{attrs['text']}</citetitle>)
  end
end

class PersonInlineMacro < Extensions::InlineMacroProcessor
  use_dsl

  named :person
  name_positional_attributes 'personname', 'format_str'

  def process parent, target, attrs
    if attrs.key? 'format_str'
      format_str = attrs['format_str']
      name_parts = attrs['personname'].split(' ')
      
      format_str = format_str.sub('{f}', '{0}')
      format_str = format_str.sub('{l}', "{%d}" % [name_parts.length-1])
      name_string = format_str
      
      for i in 0..name_parts.length-1
        name_string = name_string.sub("{%d}" % [i], name_parts[i])
      end
      
      name_string = name_string.sub('({[a-z]+}|{[0-9]+})', '')
    else
      name_string = attrs['personname']
    end
    %(<person><personname>#{name_string}</personname></person>)
  end
end

Extensions.register do
  inline_macro GlossTermInlineMacro
  inline_macro PersonInlineMacro
  inline_macro OlinkInlineMacro
  inline_macro CiteTitleInlineMacro
end
