package com.yoctopuce.examples.yocto_graph.hubs;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPIContext;
import com.yoctopuce.examples.helpers.Hub;
import com.yoctopuce.examples.helpers.HubStorage;
import com.yoctopuce.examples.yocto_graph.PreferenceHubStorage;
import com.yoctopuce.examples.yocto_graph.R;

import java.util.Locale;
import java.util.UUID;

public class EditHubFragment extends Fragment
{
    public static final String ARG_UUID = "uuid";

    private EditText _hostnameEditText;
    private EditText _usernameEditText;
    private EditText _passwordEditText;
    private EditText _portEditText;
    private Button _testButton;
    private boolean _isNewHub;
    private Hub _hub;
    private ProgressBar _progress;
    private TextView _result;
    private View _restultLayout;
    private HubStorage _hubStorage;
    private String _originalHost;
    private int _originalPort;
    private String _originalUser;
    private String _originalPass;
    private EditText _subDomainEditText;
    private String _originalProto;
    private String _originalSubDomain;


    public static EditHubFragment getFragment(UUID hubUUID)
    {
        Bundle args = new Bundle();
        args.putString(ARG_UUID, hubUUID.toString());
        final EditHubFragment fragment = new EditHubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditHubFragment getFragment()
    {
        Bundle args = new Bundle();
        final EditHubFragment fragment = new EditHubFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            final String uuid_str = getArguments().getString(ARG_UUID);
            _hubStorage = PreferenceHubStorage.Get(getContext());
            _isNewHub = uuid_str == null;
            if (_isNewHub) {
                _hub = new Hub(false);
            } else {
                UUID uuid = UUID.fromString(uuid_str);
                _hub = _hubStorage.getHub(uuid);
            }
            _originalProto = _hub.getProto();
            _originalHost = _hub.getHost();
            _originalSubDomain = _hub.getSubDomain();
            _originalPort = _hub.getPort();
            _originalUser = _hub.getUser();
            _originalPass = _hub.getPass();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_edit_hub, container, false);
        _hostnameEditText = view.findViewById(R.id.hostname);
        _portEditText = view.findViewById(R.id.port);
        _subDomainEditText = view.findViewById(R.id.subdomain);
        _usernameEditText = view.findViewById(R.id.username);
        _passwordEditText = view.findViewById(R.id.password);
        _progress = view.findViewById(R.id.test_progress);
        _restultLayout = view.findViewById(R.id.result_layout);
        _result = view.findViewById(R.id.result);
        _testButton = view.findViewById(R.id.test_button);
        _testButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                validateHubData();
                _restultLayout.setVisibility(View.VISIBLE);
                _testButton.setEnabled(false);
                _progress.setVisibility(View.VISIBLE);
                _progress.setProgress(1);
                _result.setText("");
                new TestHub().execute(_hub.getUrl(true, true));
            }
        });

        _hostnameEditText.setText(_hub.getHost());
        _portEditText.setText(String.format(Locale.US, "%d", _hub.getPort()));
        _subDomainEditText.setText(_hub.getSubDomain());
        _usernameEditText.setText(_hub.getUser());
        _passwordEditText.setText(_hub.getPass());

        return view;
    }

    private boolean validateHubData()
    {
        _hub.setProto("ws");
        _hub.setHost(_hostnameEditText.getText().toString());
        final String port_str = _portEditText.getText().toString();
        try {
            final int port = Integer.parseInt(port_str);
            _hub.setPort(port);
        } catch (NumberFormatException ignored) {
            return false;
        }
        String subDomain = _subDomainEditText.getText().toString();
        _hub.setSubDomain(subDomain);
        _hub.setUser(_usernameEditText.getText().toString());
        _hub.setPass(_passwordEditText.getText().toString());
        return true;
    }

    public boolean checkBackAllowed()
    {
        boolean unsavedChange = hasUnsavedChange();
        if (unsavedChange) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            FragmentActivity activity = getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            FragmentActivity activity = getActivity();
            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.confirmation);
                builder.setMessage(R.string.unsaved_change_msg);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, dialogClickListener);
                builder.setNegativeButton(R.string.cancel, dialogClickListener);
                builder.show();
            }
            return false;
        }
        return true;
    }

    private boolean hasUnsavedChange()
    {
        validateHubData();
        if (!_originalProto.equals(_hub.getProto())) {
            return true;
        }
        if (!_originalHost.equals(_hub.getHost())) {
            return true;
        }
        if (_originalPort != _hub.getPort()) {
            return true;
        }
        if (!_originalSubDomain.equals(_hub.getSubDomain())) {
            return true;
        }
        if (!_originalUser.equals(_hub.getUser())) {
            return true;
        }
        if (!_originalPass.equals(_hub.getPass())) {
            return true;
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    class TestHub extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings)
        {
            String url = strings[0];
            final YAPIContext yctx = new YAPIContext();
            try {
                yctx.TestHub(url, 5000);
            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String error)
        {
            _testButton.setEnabled(true);
            _progress.setProgress(100);
            _progress.setVisibility(View.GONE);
            if (error != null) {
                _result.setText(error);
            } else {
                _result.setText(R.string.success);
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.edit_hub_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return false;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                boolean nochange = checkBackAllowed();
                if (nochange) {
                    activity.finish();
                }
                return true;
            case R.id.save_menu:
                final boolean isValid = validateHubData();
                if (isValid) {
                    if (_isNewHub) {
                        _hubStorage.addNewHub(_hub);
                    } else {
                        _hubStorage.updateHub(_hub);
                    }
                    activity.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
