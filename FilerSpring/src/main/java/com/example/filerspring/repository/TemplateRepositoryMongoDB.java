package com.example.filerspring.repository;

import com.example.filerspring.model.FormAndTemplate;
import com.example.filerspring.model.FormField;
import com.mongodb.client.*;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.util.StringUtil;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class TemplateRepositoryMongoDB
        implements TemplateRepository {

    public String splitterName = "(:)";
    private MongoClient mongoClient;
    private MongoDatabase database;

    public TemplateRepositoryMongoDB(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.database = this.mongoClient.getDatabase("templateDb");
    }

    @Override
    public List<FormAndTemplate> getNamesAllTemplates() {
        MongoDatabase database = mongoClient.getDatabase("formsDB");

        // Преобразуем итератор в поток, мапаем каждый элемент в новый объект и собираем в список
        return StreamSupport
                .stream(database.listCollectionNames().spliterator(), false)
                .map(name -> new FormAndTemplate(name.split("\\(:\\)")[0], name.split("\\(:\\)")[1]))
                .collect(Collectors.toList());
    }

    @Override
    public List<FormField> getTemplateByName(String nameTemplate) {

        List<FormField> formFieldList = new ArrayList<>();
        var listNameCol = database.listCollectionNames();
        for (var el : listNameCol) {
            System.out.println(el);
        }
        System.out.println("Name Template:" + nameTemplate);
        var colTemplate = database.getCollection(nameTemplate);

        try (MongoCursor<Document> cursor = colTemplate.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Object valueField = doc.get("value_field");
                formFieldList.add(new FormField(
                        doc.get("name_field").toString(),
                        doc.get("tag_field").toString(),
                        Objects.nonNull(valueField) ? valueField.toString() : "",
                        doc.get("hint_field").toString()
                ));
            }
        }
        return formFieldList;
    }

    @Override
    public void addNewTemplate(String nameForm, String nameTemplate, List<FormField> listFormField) {
        deleteTemplateByName(nameForm + splitterName + nameTemplate);
        MongoCollection<Document> collection = database.getCollection(nameForm + splitterName + nameTemplate);

        for (var el : listFormField) {
            Document employee = new Document()
                    .append("name_field", el.getName_field())
                    .append("tag_field", el.getTag_field())
                    .append("value_field", el.getValue_field())
                    .append("hint_field", el.getHint());
            collection.insertOne(employee);
        }

        System.out.println("Success");
    }

    @Override
    public void dropALL() {
        database.drop();
    }

    @Override
    public void deleteTemplateByName(String fullTemplateName) {
        boolean collectionExists = database.listCollectionNames()
                .into(new ArrayList<>())
                .contains(fullTemplateName);
        if (collectionExists) {
            database.getCollection(fullTemplateName).drop();
        }
    }
}
