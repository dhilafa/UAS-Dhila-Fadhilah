package com.example.uasdhilafadhilah.dashboard.fragment.machine;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.uasdhilafadhilah.R;
import com.example.uasdhilafadhilah.dashboard.fragment.model.Machine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateMachineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateMachineFragment extends Fragment implements View.OnClickListener {
    private EditText inptMachineId, inptMachineName;
    private Button btnEdit, btnCancel, btnDatePicker;
    private DatePickerDialog datePickerDialog;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpdateMachineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment updateMachineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateMachineFragment newInstance(String param1, String param2) {
        UpdateMachineFragment fragment = new UpdateMachineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_machine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnEdit = view.findViewById(R.id.btnEditMachine);
        btnCancel = view.findViewById(R.id.btnCancelEdit);
        btnDatePicker = view.findViewById(R.id.datePickerEdit);
        inptMachineId = view.findViewById(R.id.machineIdEdit);
        inptMachineName = view.findViewById(R.id.machineNameEdit);
        progressBar = view.findViewById(R.id.progressBarMachine);

        initDatePicker();
        initFirestoreData();

        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDatePicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEditMachine:
                progressBar.setVisibility(View.VISIBLE);
                String id = inptMachineId.getText().toString().trim();
                String name = inptMachineName.getText().toString().trim();
                String date = btnDatePicker.getText().toString().trim();
                String machineDocument = getArguments().getString("uDocument");

                firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore.collection("machines").document(machineDocument);

                Machine machine = new Machine(id, name, date);

                documentReference
                        .update("id", machine.getId(), "name", machine.getName(), "date", machine.getDate())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Data machine edited", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                getActivity().onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Data failed to edited, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                break;
            case R.id.btnCancelEdit:
                getActivity().onBackPressed();
                break;
            case R.id.datePickerEdit:
                datePickerDialog.show();
                break;
        }
    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                btnDatePicker.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = R.style.Theme_MaterialComponents_Light_Dialog_Alert;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return day + "/" + month + "/" + year;
    }

    private void initFirestoreData() {
        String machineDocument = getArguments().getString("uDocument");
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("machines").document(machineDocument);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Machine machine = documentSnapshot.toObject(Machine.class);
                        inptMachineId.setText(machine.getId());
                        inptMachineName.setText(machine.getName());
                        btnDatePicker.setText(machine.getDate());
                    } else {
                        Toast.makeText(getActivity(), "Document not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Something went wrong, " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Something went wrong, " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}