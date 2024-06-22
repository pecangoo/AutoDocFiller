package com.example.filerspring.repository;

import com.example.filerspring.model.FormDataDTO;
import com.mongodb.client.*;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FormRepositoryMongoImpl
        implements FormRepository{

    private MongoClient mongoClient;
    private MongoDatabase database;

    public FormRepositoryMongoImpl
            (MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.database = this.mongoClient
                .getDatabase("formsDB");
    }


    @Override
    public void addNewForm(FormDataDTO data) {

        deleteFormByName(data.getNameForm());

        //TODO: Нужно ли проверять на повторность?
        MongoCollection<Document> collection
                = database.getCollection(data.getNameForm());
        for (int i = 0; i < data.getNameList().size(); i++) {
            Document employee = new Document()
                    .append("name_field",
                            data.getNameList().get(i))
                    .append("tag_field",
                            data.getTagList().get(i))
                    .append("hint_field",
                            data.getHintList().get(i));
            collection.insertOne(employee);
        }

        System.out.println("Success");
    }


    @Override
    public FormDataDTO getFormByName(String nameForm) {
        var formDataDto = new FormDataDTO();
        formDataDto.setNameForm(nameForm);

        var nameFieldList = new ArrayList<String>();
        var tagFieldList = new ArrayList<String>();
        var hintFieldList = new ArrayList<String>();

//        MongoDatabase database = mongoClient.getDatabase("formsDB");
        var col = database.getCollection(nameForm);
        MongoCursor<Document> cursor = col.find().iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                nameFieldList.add(doc.get("name_field").toString());
                tagFieldList.add(doc.get("tag_field").toString());
                hintFieldList.add(doc.get("hint_field").toString());
//                System.out.println(doc.toJson());
            }
        } finally {
            cursor.close();
        }
        formDataDto.setNameList(nameFieldList);
        formDataDto.setTagList(tagFieldList);
        formDataDto.setHintList(hintFieldList);
        return formDataDto;
    }

    @Override
    public List<String> getAllListNamesForms() {

        List<String> listNamesForms = new ArrayList<>();
        MongoDatabase database = mongoClient.getDatabase("formsDB");
//        MongoIterable<String> collectionNames = database.listCollectionNames();
//        MongoCursor<String> it = collectionNames.iterator();
//        while (it.hasNext()) {
//            String collectionName = it.next();
//            listNamesForms.add(collectionName);
//
//        }
        for (String collectionName : database.listCollectionNames()) {
            listNamesForms.add(collectionName);
        }

        return listNamesForms;
    }

    @Override
    public void dropALL() {
        database.drop();
    }

    @Override
    public void deleteFormByName(String nameForm) {
        boolean collectionExists = isCollectionExists(nameForm);
//                database.listCollectionNames()
//                        .into(new ArrayList<>())
//                        .contains(nameForm);

        if (collectionExists){
            database
                    .getCollection(nameForm)
                    .drop();
        }
    }


    private boolean isCollectionExists(String nameForm) {
        return database.listCollectionNames()
                .into(new ArrayList<>())
                .contains(nameForm);
    }
}
