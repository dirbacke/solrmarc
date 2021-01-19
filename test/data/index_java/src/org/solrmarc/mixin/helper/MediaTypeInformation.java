package org.solrmarc.mixin.helper;

import java.util.List;
import java.util.Locale;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

public class MediaTypeInformation {
    private Record record;
    private String leader;
    private String field007;
    private String field008;

    public void parseFields() {
        this.leader = this.record.getLeader().toString();
        ControlField controlField007 = (ControlField) this.record.getVariableField("007");
        ControlField controlField008 = (ControlField) this.record.getVariableField("008");
        field007 = (controlField007 != null && controlField007.getData() != null) ? controlField007.getData() : String.format("%030d", 0);
        field008 = (controlField008 != null && controlField008.getData() != null) ? controlField008.getData() : String.format("%030d", 0);
    }

    public boolean isCharInPosField007(char c, int pos) {
       return (pos < field007.length()) && field007.charAt(pos) == c;
    }

    public boolean isCharInPosField008(char c, int pos) {
        return (pos < field008.length()) && field008.charAt(pos) == c;
    }

    public List<VariableField> getField500() {
        return record.getVariableFields("500");
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getField007() {
        return field007;
    }

    public void setField007(String field007) {
        this.field007 = field007;
    }

    public boolean isMp3() {
        String field300 = getSubfield("300", 'a');
        String field939b = getSubfield("939", 'b');
        return (field300.toLowerCase().indexOf("mp3") != -1 || field939b.toLowerCase().indexOf("mp3") != -1);
    }

    public boolean isEasyToRead() {
        String field655a = getSubfield("655", 'a');
        String field697a = getSubfield("697", 'c');
        String compareString = "lättläst";
        return (field655a.indexOf(compareString) != -1 || field697a.indexOf(compareString) != -1 );
    }

    public boolean isAnnouncedMedia(String compareString) {
        compareString = compareString.toLowerCase(Locale.forLanguageTag("sv-SE"));
        String field596b = getSubfield("596", 'b');
        String field500a = getSubfield("500", 'a');
        return (field596b.indexOf(compareString) != -1 || field500a.indexOf(compareString) != -1);
    }

    public boolean isAnnouncedGame(String compareString) {
        compareString = compareString.toLowerCase(Locale.forLanguageTag("sv-SE"));
        String field538a = getSubfield("538", 'a');
        return (field538a.indexOf(compareString) != -1);
    }

    public boolean isPocket() {
        String field020q = getSubfield("020", 'q');
        String compareString = "pocket";
        return (field020q.indexOf(compareString) != -1);
    }

    public boolean isLargeScale() {
        String field250a = getSubfield("250", 'a');
        String field340n = getSubfield("340", 'n');
        String largeScale = "storstil", largeScaleSpace = "stor stil";
        return field250a.indexOf(largeScale) != -1 || field340n.indexOf(largeScaleSpace) != -1;
    }

    private String getSubfield(String field, char section) {
       String data = "";
       for (VariableField variableField : record.getVariableFields(field)) {
           if(((DataField) variableField).getSubfield(section) != null) {
               if(((DataField) variableField).getSubfield(section).getData() != null) {
                   data += ((DataField) variableField).getSubfield(section).getData().toLowerCase(Locale.forLanguageTag("sv-SE"));
               }
           }
       }
       return data;

    }
}