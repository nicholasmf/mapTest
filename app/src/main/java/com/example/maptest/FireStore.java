package com.example.maptest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class FireStore {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addPoints() {
        Map<String, LatLng> positions = new HashMap<>();
        positions.put("sydney", new LatLng(-34, 151));
        positions.put("foo", new LatLng(42, 69));
        positions.put("bar", new LatLng(-69, -42));

        db.collection("positions")
                .add(positions)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getAllPoints() {
        db.collection("positions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void setDocument(String docName) {
        Map<String, LatLng> positions = new HashMap<>();
        positions.put("sydney", new LatLng(-34, 151));
        positions.put("foo", new LatLng(42, 69));
        positions.put("bar", new LatLng(-69, -42));

        db.collection("positions")
                .document(docName)
                .set(positions);
    }

    public void setDocument(String colName, String docName, Object object) {
        db.collection(colName)
                .document(docName)
                .set(object);
    }

    public void getDocument(String docName) {
        db.collection("positions").document(docName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    }
                });
    }

    public void getDocument(String colName, String docName) {
        db.collection(colName).document(docName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Building building = document.toObject(Building.class);
                                Log.d(TAG, building.toString());
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    }
                });
    }

    public void getDocument(String colName, String docName, final DatabaseLoaded dbLoaded) {
        db.collection(colName).document(docName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Building building = document.toObject(Building.class);
                                Log.d(TAG, building.toString());
                                dbLoaded.databaseLoaded(building);
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    }
                });
    }
}
