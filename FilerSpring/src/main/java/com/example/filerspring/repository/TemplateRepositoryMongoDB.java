package com.example.filerspring.repository;

import com.example.filerspring.model.FormAndTemplate;
import com.example.filerspring.model.FormField;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TemplateRepositoryMongoDB
        implements TemplateRepository {

    public String splitterName = "(:)";
    private MongoClient mongoClient;
    private MongoDatabase database;

    public TemplateRepositoryMongoDB(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.database = this.mongoClient
                .getDatabase("templateDb");
    }

    @Override
    public List<FormAndTemplate> getNamesAllTemplates() {

        List<FormAndTemplate> resList = new ArrayList<FormAndTemplate>();
        var listNameCol = database.listCollectionNames();

        for (var name : listNameCol) {
            System.out.print("ALL:");
            resList.add(new FormAndTemplate(name.split("\\(:\\)")[0],
                    name.split("\\(:\\)")[1]
            ));
        }
        return resList;
    }

    @Override
    public List<FormField> getTemplateByName(String nameTemplate) {

        List<FormField> formFieldList = new ArrayList<>();
        var listNameCol = database.listCollectionNames();
        for (var el : listNameCol) {
            System.out.println(el);
        }
        System.out.println("Name Template:" + nameTemplate);
        var colTemplate
                = database.getCollection(nameTemplate);


        MongoCursor<Document> cursor = colTemplate.find().iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String value_f = "";
                if(doc.get("value_field") != null)
                    value_f = doc.get("value_field").toString();

                formFieldList.add(new FormField(
                        doc.get("name_field").toString(),
                doc.get("tag_field").toString(),
                value_f,
                doc.get("hint_field").toString()
                ));
            }
        } finally {
            cursor.close();
        }
        return formFieldList;
    }

    @Override
    public void addNewTemplate(String nameForm, String nameTemplate, List<FormField> listFormField) {

        deleteTemplateByName(nameForm +splitterName +  nameTemplate);
        MongoCollection<Document> collection
                = database.getCollection(nameForm + splitterName + nameTemplate);

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
        boolean collectionExists =
                database.listCollectionNames()
                        .into(new ArrayList<>())
                        .contains(fullTemplateName);
        if (collectionExists) {
            database
                    .getCollection(fullTemplateName)
                    .drop();
        }
    }
}
