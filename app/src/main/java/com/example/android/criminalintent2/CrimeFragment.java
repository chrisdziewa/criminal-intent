package com.example.android.criminalintent2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chris on 1/31/2017.
 */

public class CrimeFragment extends Fragment {
    private static final String CRIME_FRAGMENT = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";

    // Dialog tags
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_IMAGE = "DialogImage";

    // Activity extras
    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";

    // request codes
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;
    public static final int ACTIVITY_REQUEST_DATE = 3;
    public static final int ACTIVITY_REQUEST_TIME = 4;
    public static final int REQUEST_CONTACT = 5;

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 6;

    // Formatting date/time
    private static final int DATE_FORMAT = DateFormat.FULL;
    private static final int TIME_FORMAT = DateFormat.SHORT;

    // Widgets
    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private ViewTreeObserver mPhotoTreeObserver;

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    private Point mPhotoViewSize;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        setHasOptionsMenu(true);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Also intentionally left blank
            }
        });

        // Setup date button
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        final Date currentDate = mCrime.getDate();

        String formattedDate = DateFormatter.formatDateAsString(DATE_FORMAT, currentDate);
        mDateButton.setText(formattedDate);

        // Setup time button
        mTimeButton = (Button) v.findViewById(R.id.crime_time_button);
        String formattedTime = DateFormatter.formatDateAsTimeString(TIME_FORMAT, currentDate);
        mTimeButton.setText(formattedTime);

        // Button on click listeners
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTablet(getContext())) {
                    FragmentManager manager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dialog.show(manager, DIALOG_DATE);
                } else {
                    // Screen is smaller - display dialog as full screen activity
                    Intent intent = new Intent(getContext(), DatePickerActivity.class);
                    intent.putExtra(EXTRA_DATE, mCrime.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_DATE);
                }
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTablet(getContext())) {
                    FragmentManager manager = getFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                    dialog.show(manager, DIALOG_TIME);
                } else {
                    // Screen is smaller - display dialog as full screen activity
                    Intent intent = new Intent(getContext(), TimePickerActivity.class);
                    intent.putExtra(EXTRA_TIME, mCrime.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_TIME);
                }
            }
        });

        // Setup checkbox
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use intent builder to create a send action
                // that opens a populated email
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .startChooser();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallSuspectButton = (Button) v.findViewById(R.id.crime_call_suspect);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCrime.getSuspect() == null) {
                    Toast.makeText(getActivity(), getString(R.string.toast_no_suspect), Toast.LENGTH_SHORT).show();
                    return;
                }
                checkReadContactsPermission();
            }
        });

        // Disable the choose suspect button to prevent crash
        // when no contacts app is available
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        // Setup photo taking abilities
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.criminalintent2.fileprovider",
                        mPhotoFile);

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager()
                        .queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.toString(),
                            uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    startActivityForResult(captureImage, REQUEST_PHOTO);
                }
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);

        // On image click, open zoomed image dialog
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                ImageDisplayFragment fragment = ImageDisplayFragment.newInstance(mPhotoFile);
                fragment.show(fragmentManager, DIALOG_IMAGE);
            }
        });

        mPhotoTreeObserver = mPhotoView.getViewTreeObserver();
        mPhotoTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPhotoViewSize = new Point();
                mPhotoViewSize.set(mPhotoView.getWidth(), mPhotoView.getHeight());

                updatePhotoView();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE || requestCode == REQUEST_TIME ||
                requestCode == ACTIVITY_REQUEST_DATE || requestCode == ACTIVITY_REQUEST_TIME) {
            final Date date;
            if (requestCode == REQUEST_DATE || requestCode == ACTIVITY_REQUEST_DATE) {
                date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            } else {
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            }

            mCrime.setDate(date);
            mDateButton.setText(DateFormatter.formatDateAsString(DATE_FORMAT, date));
            mTimeButton.setText(DateFormatter.formatDateAsTimeString(TIME_FORMAT, date));
            updateCrime();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that results were actually received
                if (c != null && c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                c.moveToFirst();
                int suspectIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                int suspectIdIndex = c.getColumnIndex(ContactsContract.Contacts._ID);

                String suspect = c.getString(suspectIndex);
                int suspectId = c.getInt(suspectIdIndex);

                mCrime.setSuspect(suspect);
                mCrime.setSuspectId(suspectId);
                mSuspectButton.setText(suspect);
                updateCrime();
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.example.android.criminalintent2.fileprovider", mPhotoFile);
            // Remove temporary write access to file from camera
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }
    }

    // For sending a formatted email of the report
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        Date date = mCrime.getDate();
        String dateString = DateFormatter.formatDateAsString(DATE_FORMAT, date);

        String timeString = DateFormatter.formatDateAsTimeString(TIME_FORMAT, date);

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        // Return the report
        return getString(R.string.crime_report,
                mCrime.getTitle(), dateString, timeString, solvedString, suspect);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setClickable(false);
        } else {
            mPhotoView.setClickable(true);
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoViewSize.x, mPhotoViewSize.y);
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    // For determining dialog as dialog or fragment
    // from http://www.androidcodesnippets.com/2016/02/check-if-device-is-tablet-or-phone/
    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(ARG_CRIME_ID, mCrime.getId());

        super.onSaveInstanceState(outState);
    }

    private void deleteCrime() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimeLab.deleteCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);

        // Delete the image file associated with crime if exists

        File file = new File(mPhotoFile.getPath());
        if (file.exists()) {
            file.delete();
            Log.i("CrimeFragment", "deleteCrime photo called");
        }

        Toast.makeText(getActivity(), R.string.toast_delete_crime, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                    deleteCrime();
                    getActivity().finish();
                } else {
                    // Using tablet with two pane view (Clear out fragment
                    deleteCrime();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .remove(this)
                            .commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkReadContactsPermission() {// Here, thisActivity is the current activity
        Log.i(CRIME_FRAGMENT, "checkReadContactsPermission: start");
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.i(CRIME_FRAGMENT, "checkReadContactsPermission: called");
            callSuspect();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                Toast.makeText(getActivity(), "Dial with phone successful", Toast.LENGTH_SHORT).show();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    callSuspect();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void callSuspect() {
        Cursor c = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(mCrime.getSuspectId())},
                null
        );
        // Dial suspect if one exists

        try {
            if (c == null || c.getCount() == 0) {
                Log.i(CRIME_FRAGMENT, "callSuspect cursor null or zero");
                return;
            }
            c.moveToFirst();

            String phoneNumber = c.getString(0);

            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);

        } finally {
            c.close();
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }


}
