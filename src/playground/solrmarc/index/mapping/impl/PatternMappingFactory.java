package playground.solrmarc.index.mapping.impl;

import playground.solrmarc.index.extractor.impl.patternMapping.PatternMapping;
import playground.solrmarc.index.indexer.IndexerSpecException;
import playground.solrmarc.index.mapping.AbstractMultiValueMapping;
import playground.solrmarc.index.mapping.AbstractValueMappingFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;

public class PatternMappingFactory extends AbstractValueMappingFactory
{
    @Override
    public boolean canHandle(String mappingConfiguration)
    {
        return (mappingConfiguration.startsWith("map") || mappingConfiguration.startsWith("filter"));
    }

    // @Override
    // public AbstractSingleValueMapping createSingleValueMapping(String
    // mappingConfiguration)
    // {
    //// final String mappingName = mappingConfiguration.substring(4,
    // mappingConfiguration.length() - 1);
    //// List<PatternMapping> patternMappings =
    // PatternMappingValueExtractorFactory.getPatternMappingsForName(mappingName);
    // List<PatternMapping> patternMappings =
    // pattermMappingsFromString(mappingConfiguration.substring(4,
    // mappingConfiguration.length() - 1));
    // return new SingleValuePatternMapping(patternMappings);
    // }

    @Override
    public AbstractMultiValueMapping createMultiValueMapping(String mappingConfiguration)
    {
        // final String mappingName = mappingConfiguration.substring(4,
        // mappingConfiguration.length() - 1);
        // List<PatternMapping> patternMappings =
        // PatternMappingValueExtractorFactory.getPatternMappingsForName(mappingName);
        int parenLoc = mappingConfiguration.indexOf("(");
        return getMultiValuePattermMappingsFromString( mappingConfiguration.substring(parenLoc + 1, mappingConfiguration.length() - 1));
    }

    @Override
    public AbstractMultiValueMapping createMultiValueMapping(String[] mapParts)
    {
        List<PatternMapping> patternMappings;
        if (mapParts.length > 2)
        {
            return(getMultiValuePattermMappingsFromStrings(mapParts, 1));
        }
        else
        {
            return(getMultiValuePattermMappingsFromString(mapParts[1]));
        }
     //   return new MultiValuePatternMapping(patternMappings, isFilter);
    }

    public static MultiValuePatternMapping getMultiValuePattermMappingsFromString(String mapSpec)
    {
        final String mapParts[] = mapSpec.split("[|][|]");
        return getMultiValuePattermMappingsFromStrings(mapParts, 0);
    }
    
    public static MultiValuePatternMapping getMultiValuePattermMappingsFromStrings(String[] mapParts, int offset)
    {
        List<PatternMapping> pm = new ArrayList<PatternMapping>(mapParts.length);
        boolean matchAll = false;
        boolean keepRaw = false;
        for (int i = offset; i < mapParts.length; i++)
        {
            String mapEntry[] = mapParts[i].split("[ ]*=>[ ]*", 2);
            if (mapEntry.length == 2)
            {
                try
                {
                    pm.add(new PatternMapping(mapEntry[0], mapEntry[1], i));
                }
                catch (PatternSyntaxException pse)
                {
                    throw new IndexerSpecException(
                            "Malformed regular expression in pattern map : " + mapEntry[0] + "\n" + pse.getMessage());
                }
                catch (IndexOutOfBoundsException ioobe)
                {
                    throw new IndexerSpecException(
                            "Unknown group in replacement string : " + mapEntry[1] + "\n" + ioobe.getMessage());
                }
            }
            else if (mapEntry.length == 1 && mapEntry[0].equals("matchAll"))
            {
                matchAll = true;
            }
            else if (mapEntry.length == 1 && mapEntry[0].equals("keepRaw"))
            {
                keepRaw = true;
            }
        }
        return new MultiValuePatternMapping(pm, matchAll, keepRaw);
    }
    
    public static MultiValuePatternMapping pattermMappingsFromPatternProperties(Properties props)
    {
        List<PatternMapping> pm = new ArrayList<PatternMapping>(props.size());
        boolean matchAll = false;
        boolean keepRaw = false;

        for (int i = 0; i < props.size(); i++)
        {
            String key = "pattern_"+i;
            String value = props.getProperty(key);
            String mapEntry[] = value.split("[ ]*=>[ ]*", 2);
            if (mapEntry.length == 2)
            {
                try
                {
                    pm.add(new PatternMapping(mapEntry[0], mapEntry[1], i));
                }
                catch (PatternSyntaxException pse)
                {
                    throw new IndexerSpecException(
                            "Malformed regular expression in pattern map : " + mapEntry[0] + "\n" + pse.getMessage());
                }
                catch (IndexOutOfBoundsException ioobe)
                {
                    throw new IndexerSpecException(
                            "Unknown group in replacement string : " + mapEntry[1] + "\n" + ioobe.getMessage());
                }
            }
            else if (mapEntry.length == 1 && mapEntry.equals("matchAll"))
            {
                matchAll = true;
            }
            else if (mapEntry.length == 1 && mapEntry.equals("keepRaw"))
            {
                keepRaw = true;
            }
        }
        return new MultiValuePatternMapping(pm, matchAll, keepRaw);
    }

}
