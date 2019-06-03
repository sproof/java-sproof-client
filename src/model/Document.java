package io.sproof.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Model-Klasse fuer ein Sproof Dokument
 */
@Data
public class Document extends AbstractSproofData {

    private Long validFrom;
    private Long validUntil;
    private String documentHash;
    private String data;
    private String locationHash;
    private String name;

    private List<String> dependencies = new ArrayList<>();
    private List<String> receiverAttributes = new ArrayList<>();
    private List<String> receivers = new ArrayList<>();

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }

    public void addReceiverAttribute(String rec) {
        receiverAttributes.add(rec);
    }

    public void addReceiver(String rec) {
        receivers.add(rec);
    }

}
