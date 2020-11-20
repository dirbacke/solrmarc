package org.solrmarc.mixin.helper;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.Record;

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

    public String getField008() {
        return field008;
    }

    public void setField008(String field008) {
        this.field008 = field008;
    }
}