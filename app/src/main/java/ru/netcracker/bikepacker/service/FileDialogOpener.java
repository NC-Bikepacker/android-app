package ru.netcracker.bikepacker.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.netcracker.bikepacker.R;

public class FileDialogOpener {
    public String Default_File_Name = "default.gpx";
    private final int fileOpenMode = 0;
    private final int fileSaveMode = 1;
    private final int fileChooseDirMode = 2;
    private int selectModeType = fileSaveMode;
    private final Context context;
    private String sdcardDirectory = "";
    private TextView titleView1;
    private TextView titleView;
    private String Selected_File_Name = Default_File_Name;
    private EditText input_text;

    private String dir = "";
    private List<String> subdirs = null;
    private FileDialogOpenerListener fileDialogOpenerListener = null;
    private ArrayAdapter<String> listAdapter = null;

    public FileDialogOpener(Context context, String file_select_type, FileDialogOpenerListener FileDialogOpenerListener) {
        if (file_select_type.equals("FileOpen")) selectModeType = fileOpenMode;
        else if (file_select_type.equals("FileSave")) selectModeType = fileSaveMode;
        else if (file_select_type.equals("FolderChoose")) selectModeType = fileChooseDirMode;
        else selectModeType = fileOpenMode;

        this.context = context;
        sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileDialogOpenerListener = FileDialogOpenerListener;

        try {
            sdcardDirectory = new File(sdcardDirectory).getCanonicalPath();
        } catch (IOException ioe) {
            Log.e("FileDialogOpener Error", ioe.getMessage());
        }
    }

    //  chooseFile_or_Dir() - load directory chooser dialog for initial
//  default sdcard directory
    public void chooseFile_or_Dir() {
        // Initial directory is sdcard directory
        if (!StringUtils.isBlank(dir)) chooseFile_or_Dir(sdcardDirectory);
        else chooseFile_or_Dir(dir);
    }

    public void chooseFile_or_Dir(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dir = sdcardDirectory;
        }

        try {
            dir = new File(dir).getCanonicalPath();
        } catch (IOException ioe) {
            return;
        }

        this.dir = dir;
        subdirs = getDirectories(dir);

        class FileDialogOpenerOnClickListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int item) {
                String m_dir_old = FileDialogOpener.this.dir;
                String sel = "" + ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
                if (sel.charAt(sel.length() - 1) == '/') sel = sel.substring(0, sel.length() - 1);

                // Navigate into the sub-directory
                if (sel.equals("..")) {
                    FileDialogOpener.this.dir = FileDialogOpener.this.dir.substring(0, FileDialogOpener.this.dir.lastIndexOf("/"));
                } else {
                    FileDialogOpener.this.dir += "/" + sel;
                }
                Selected_File_Name = Default_File_Name;

                if ((new File(FileDialogOpener.this.dir).isFile())) // If the selection is a regular file
                {
                    FileDialogOpener.this.dir = m_dir_old;
                    Selected_File_Name = sel;
                }

                updateDirectory();
            }
        }

        AlertDialog.Builder dialogBuilder = createDirectoryChooserDialog(dir, subdirs,
                new FileDialogOpenerOnClickListener());

        dialogBuilder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//              Current directory chosen
//              Call registered listener supplied with the chosen directory
                if (fileDialogOpenerListener != null) {
                    {
                        if (selectModeType == fileOpenMode || selectModeType == fileSaveMode) {
                            Selected_File_Name = input_text.getText() + "";
                            fileDialogOpenerListener.onChosenDir(FileDialogOpener.this.dir, Selected_File_Name);
                        } else {
                            fileDialogOpenerListener.onChosenDir(FileDialogOpener.this.dir, Selected_File_Name);
                        }
                    }
                }
            }
        }).setNegativeButton("Cancel", null);

        final AlertDialog dirsDialog = dialogBuilder.create();
        dirsDialog.show();
    }

    private boolean createSubDir(String newDir) {
        File newDirFile = new File(newDir);
        if (!newDirFile.exists()) return newDirFile.mkdir();
        else return false;
    }

    private List<String> getDirectories(String dir) {
        List<String> dirs = new ArrayList<String>();

        try {
            File dirFile = new File(dir);

            // if directory is not the base sd card directory add ".." for going up one directory
            if (!StringUtils.equals(this.dir, sdcardDirectory)) dirs.add("..");

            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return dirs;
            }

            File[] listFiles = dirFile.listFiles();

            for (File file : listFiles) {
                if (file.isDirectory()) {
                    // Add "/" to directory names to identify them in the list
                    dirs.add(file.getName() + "/");
                } else if (selectModeType == fileSaveMode || selectModeType == fileOpenMode) {
                    // Add file names to the list if we are doing a file save or file open operation
                    dirs.add(file.getName());
                }
            }
        } catch (Exception e) {
            Log.e("Getting directories error", e.getMessage());
        }

        dirs.sort(String::compareTo);
        return dirs;
    }

    //    START DIALOG DEFINITION
    @SuppressLint("ResourceAsColor")
    private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
                                                             DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//      Create title text showing file select type
        titleView1 = new TextView(context);
        titleView1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        titleView1.setTextAppearance(context, android.R.style.TextAppearance_Large);
        titleView1.setTextColor(R.color.black);

        if (selectModeType == fileOpenMode) titleView1.setText("Open file");
        if (selectModeType == fileSaveMode) titleView1.setText("Save file as:");
        if (selectModeType == fileChooseDirMode) titleView1.setText("Folder select:");

        titleView1.setGravity(Gravity.CENTER_VERTICAL);
        titleView1.setBackgroundColor(R.color.bikepacker_purple);
        titleView1.setTextColor(R.color.white);

//      Create custom view for AlertDialog title
        LinearLayout titleLayout1 = new LinearLayout(context);
        titleLayout1.setOrientation(LinearLayout.VERTICAL);
        titleLayout1.addView(titleView1);


        if (selectModeType == fileChooseDirMode || selectModeType == fileSaveMode) {
//            Create New Folder Button
            Button newDirButton = new Button(context);
            newDirButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            newDirButton.setText("New Folder");
            newDirButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    final EditText input = new EditText(context);

                                                    // Show new folder name input dialog
                                                    new AlertDialog.Builder(context).
                                                            setTitle("New Folder Name").
                                                            setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            Editable newDir = input.getText();
                                                            String newDirName = "";

                                                            if (newDir != null) {
                                                                newDirName = newDir.toString();
                                                            }

                                                            // Create new directory
                                                            if (createSubDir(dir + "/" + newDirName)) {
                                                                // Navigate into the new directory
                                                                dir += "/" + newDirName;
                                                                updateDirectory();
                                                            } else {
                                                                Toast.makeText(context, "Failed to create '"
                                                                        + newDirName + "' folder", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).setNegativeButton("Cancel", null).show();
                                                }
                                            }
            );
            titleLayout1.addView(newDirButton);
        }

//        Create View with folder path and entry text box
        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);

        titleView = new TextView(context);
        titleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        titleView.setBackgroundColor(R.color.bikepacker_purple);
        titleView.setTextColor(R.color.white);
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setText(title);

        titleLayout.addView(titleView);

        if (selectModeType == fileOpenMode || selectModeType == fileSaveMode) {
            input_text = new EditText(context);
            input_text.setText(Default_File_Name);
            titleLayout.addView(input_text);
        }
//        Set Views and Finish Dialog builder
        dialogBuilder.setView(titleLayout);
        dialogBuilder.setCustomTitle(titleLayout1);
        listAdapter = createListAdapter(listItems);
        dialogBuilder.setSingleChoiceItems(listAdapter, -1, onClickListener);
        dialogBuilder.setCancelable(false);
        return dialogBuilder;
    }

    private void updateDirectory() {
        subdirs.clear();
        subdirs.addAll(getDirectories(dir));
        titleView.setText(dir);
        listAdapter.notifyDataSetChanged();

        if (selectModeType == fileSaveMode || selectModeType == fileOpenMode) {
            input_text.setText(Selected_File_Name);
        }
    }

    private ArrayAdapter<String> createListAdapter(List<String> items) {
        return new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, android.R.id.text1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (v instanceof TextView) {
                    // Enable list item (directory) text wrapping
                    TextView tv = (TextView) v;
                    tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                    tv.setEllipsize(null);
                }
                return v;
            }
        };
    }

    public interface FileDialogOpenerListener {
        void onChosenDir(String chosenDir, String chosenFilename);
    }
} 