package ru.netcracker.bikepacker.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.service.ZipFileManager;

public class DialogFragment extends androidx.fragment.app.DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.dialog_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button button = view.findViewById(R.id.got_it_button);
        ZipFileManager zipFileManager = new ZipFileManager(requireContext());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zipFileManager.openUploadFileDialog(requireContext());
                dismiss();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
