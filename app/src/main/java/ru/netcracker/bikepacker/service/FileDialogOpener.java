package ru.netcracker.bikepacker.service;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.text.Editable;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.netcracker.bikepacker.R;

public class FileDialogOpener {
    public String Default_File_Name = "default.gpx";
    private int fileOpenMode = 0;
    private int fileSaveMode = 1;
    private int fileChooseDirMode = 2;
    private int selectModeType = fileSaveMode;
    private String m_sdcardDirectory = "";
    private Context m_context;
    private TextView m_titleView1;
    private TextView m_titleView;
    private String Selected_File_Name = Default_File_Name;
    private EditText input_text;

    private String m_dir = "";
    private List<String> m_subdirs = null;
    private FileDialogOpenerListener m_FileDialogOpenerListener = null;
    private ArrayAdapter<String> m_listAdapter = null;

    public FileDialogOpener(Context context, String file_select_type, FileDialogOpenerListener FileDialogOpenerListener) {
        if (file_select_type.equals("FileOpen")) selectModeType = fileOpenMode;
        else if (file_select_type.equals("FileSave")) selectModeType = fileSaveMode;
        else if (file_select_type.equals("FolderChoose")) selectModeType = fileChooseDirMode;
        else selectModeType = fileOpenMode;

        m_context = context;
        m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        m_FileDialogOpenerListener = FileDialogOpenerListener;

        try {
            m_sdcardDirectory = new File(m_sdcardDirectory).getCanonicalPath();
        } catch (IOException ioe) {
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // chooseFile_or_Dir() - load directory chooser dialog for initial
    // default sdcard directory
    ///////////////////////////////////////////////////////////////////////
    public void chooseFile_or_Dir() {
        // Initial directory is sdcard directory
        if (m_dir.equals("")) chooseFile_or_Dir(m_sdcardDirectory);
        else chooseFile_or_Dir(m_dir);
    }

    public void chooseFile_or_Dir(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dir = m_sdcardDirectory;
        }

        try {
            dir = new File(dir).getCanonicalPath();
        } catch (IOException ioe) {
            return;
        }

        m_dir = dir;
        m_subdirs = getDirectories(dir);

        class FileDialogOpenerOnClickListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int item) {
                String m_dir_old = m_dir;
                String sel = "" + ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
                if (sel.charAt(sel.length() - 1) == '/') sel = sel.substring(0, sel.length() - 1);

                // Navigate into the sub-directory
                if (sel.equals("..")) {
                    m_dir = m_dir.substring(0, m_dir.lastIndexOf("/"));
                } else {
                    m_dir += "/" + sel;
                }
                Selected_File_Name = Default_File_Name;

                if ((new File(m_dir).isFile())) // If the selection is a regular file
                {
                    m_dir = m_dir_old;
                    Selected_File_Name = sel;
                }

                updateDirectory();
            }
        }

        AlertDialog.Builder dialogBuilder = createDirectoryChooserDialog(dir, m_subdirs,
                new FileDialogOpenerOnClickListener());

        dialogBuilder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Current directory chosen
                // Call registered listener supplied with the chosen directory
                if (m_FileDialogOpenerListener != null) {
                    {
                        if (selectModeType == fileOpenMode || selectModeType == fileSaveMode) {
                            Selected_File_Name = input_text.getText() + "";
                            m_FileDialogOpenerListener.onChosenDir(m_dir, Selected_File_Name);
                        } else {
                            m_FileDialogOpenerListener.onChosenDir(m_dir, Selected_File_Name);
                        }
                    }
                }
            }
        }).setNegativeButton("Cancel", null);

        final AlertDialog dirsDialog = dialogBuilder.create();

        // Show directory chooser dialog
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
            if (!m_dir.equals(m_sdcardDirectory)) dirs.add("..");

            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return dirs;
            }

            for (File file : dirFile.listFiles()) {
                if (file.isDirectory()) {
                    // Add "/" to directory names to identify them in the list
                    dirs.add(file.getName() + "/");
                } else if (selectModeType == fileSaveMode || selectModeType == fileOpenMode) {
                    // Add file names to the list if we are doing a file save or file open operation
                    dirs.add(file.getName());
                }
            }
        } catch (Exception e) {
        }

        Collections.sort(dirs, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return dirs;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////                                   START DIALOG DEFINITION                                    //////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("ResourceAsColor")
    private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
                                                             DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);
        ////////////////////////////////////////////////
        // Create title text showing file select type //
        ////////////////////////////////////////////////
        m_titleView1 = new TextView(m_context);
        m_titleView1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        m_titleView1.setTextAppearance(m_context, android.R.style.TextAppearance_Large);
        m_titleView1.setTextColor(R.color.black);

        if (selectModeType == fileOpenMode) m_titleView1.setText("Open file");
        if (selectModeType == fileSaveMode) m_titleView1.setText("Save file as:");
        if (selectModeType == fileChooseDirMode) m_titleView1.setText("Folder select:");

        //need to make this a variable Save as, Open, Select Directory
        m_titleView1.setGravity(Gravity.CENTER_VERTICAL);
        m_titleView1.setBackgroundColor(R.color.bikepacker_purple);
        m_titleView1.setTextColor(R.color.white);

        // Create custom view for AlertDialog title
        LinearLayout titleLayout1 = new LinearLayout(m_context);
        titleLayout1.setOrientation(LinearLayout.VERTICAL);
        titleLayout1.addView(m_titleView1);


        if (selectModeType == fileChooseDirMode || selectModeType == fileSaveMode) {
            ///////////////////////////////
            // Create New Folder Button  //
            ///////////////////////////////
            Button newDirButton = new Button(m_context);
            newDirButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            newDirButton.setText("New Folder");
            newDirButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    final EditText input = new EditText(m_context);

                                                    // Show new folder name input dialog
                                                    new AlertDialog.Builder(m_context).
                                                            setTitle("New Folder Name").
                                                            setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            Editable newDir = input.getText();
                                                            String newDirName = newDir.toString();
                                                            // Create new directory
                                                            if (createSubDir(m_dir + "/" + newDirName)) {
                                                                // Navigate into the new directory
                                                                m_dir += "/" + newDirName;
                                                                updateDirectory();
                                                            } else {
                                                                Toast.makeText(m_context, "Failed to create '"
                                                                        + newDirName + "' folder", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).setNegativeButton("Cancel", null).show();
                                                }
                                            }
            );
            titleLayout1.addView(newDirButton);
        }

        /////////////////////////////////////////////////////
        // Create View with folder path and entry text box //
        /////////////////////////////////////////////////////
        LinearLayout titleLayout = new LinearLayout(m_context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);

        m_titleView = new TextView(m_context);
        m_titleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        m_titleView.setBackgroundColor(R.color.bikepacker_purple);
        m_titleView.setTextColor(R.color.white);
        m_titleView.setGravity(Gravity.CENTER_VERTICAL);
        m_titleView.setText(title);

        titleLayout.addView(m_titleView);

        if (selectModeType == fileOpenMode || selectModeType == fileSaveMode) {
            input_text = new EditText(m_context);
            input_text.setText(Default_File_Name);
            titleLayout.addView(input_text);
        }
        //////////////////////////////////////////
        // Set Views and Finish Dialog builder  //
        //////////////////////////////////////////
        dialogBuilder.setView(titleLayout);
        dialogBuilder.setCustomTitle(titleLayout1);
        m_listAdapter = createListAdapter(listItems);
        dialogBuilder.setSingleChoiceItems(m_listAdapter, -1, onClickListener);
        dialogBuilder.setCancelable(false);
        return dialogBuilder;
    }

    private void updateDirectory() {
        m_subdirs.clear();
        m_subdirs.addAll(getDirectories(m_dir));
        m_titleView.setText(m_dir);
        m_listAdapter.notifyDataSetChanged();
        //#scorch
        if (selectModeType == fileSaveMode || selectModeType == fileOpenMode) {
            input_text.setText(Selected_File_Name);
        }
    }

    private ArrayAdapter<String> createListAdapter(List<String> items) {
        return new ArrayAdapter<String>(m_context, android.R.layout.select_dialog_item, android.R.id.text1, items) {
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
        public void onChosenDir(String chosenDir, String chosenFilename);
    }
} 