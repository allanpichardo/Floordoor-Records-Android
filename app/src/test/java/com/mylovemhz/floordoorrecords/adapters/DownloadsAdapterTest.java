package com.mylovemhz.floordoorrecords.adapters;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mylovemhz.floordoorrecords.BuildConfig;
import com.mylovemhz.floordoorrecords.MainActivity;
import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.net.AlbumResponse;
import com.mylovemhz.floordoorrecords.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DownloadsAdapterTest {

    private List<AlbumResponse> responses;
    private Context context;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InputStream is = Robolectric.setupActivity(MainActivity.class)
                .getResources().openRawResource(R.raw.testalbum);
        String rawResponse = Utils.convertStreamToString(is);
        AlbumResponse albumResponse = new AlbumResponse(rawResponse);
        responses = new ArrayList<>();
        responses.add(albumResponse);
        context = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void testViewsCreated() throws Exception {
        MockDownloadsAdapter adapter = new MockDownloadsAdapter(responses);
        assertEquals(responses.size(), adapter.getItemCount());
        DownloadsAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(new LinearLayout(context), 0);
        adapter.onBindViewHolder(viewHolder,0);
        View itemView = adapter.viewHolders.get(0).itemView;
        assertNotNull(itemView);

        TextView artistText = (TextView)itemView.findViewById(R.id.artistText);
        TextView titleText = (TextView)itemView.findViewById(R.id.titleText);
        assertEquals("My Love MHz", artistText.getText().toString());
        assertEquals("0x766", titleText.getText().toString());
    }

    @Test
    public void testSelection() throws Exception {
        MockDownloadsAdapter adapter = new MockDownloadsAdapter(responses);
        DownloadsAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(new LinearLayout(context), 0);
        adapter.onBindViewHolder(viewHolder,0);
        View itemView = adapter.viewHolders.get(0).itemView;
        assertNotNull(itemView);

        CheckBox checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        assertFalse(checkBox.isChecked());
        assertEquals(0, adapter.getSelections().size());
        final CheckBox finalCheckBox = checkBox;
        adapter.setCallback(new DownloadsAdapter.Callback() {
            @Override
            public void onSelectionChanged(Set<Integer> selection) {
                if(finalCheckBox.isChecked()){
                    assertTrue(selection.size() > 0);
                }else{
                    assertTrue(selection.size() == 0);
                }
            }
        });
        checkBox.setChecked(true);
        assertEquals(1, adapter.getSelections().size());
        checkBox.setChecked(false);
        assertEquals(0, adapter.getSelections().size());

        checkBox.setChecked(true);
        Set<Integer> selection = adapter.getSelections();
        adapter = new MockDownloadsAdapter(responses);
        viewHolder = adapter.onCreateViewHolder(new LinearLayout(context), 0);
        adapter.onBindViewHolder(viewHolder,0);
        itemView = adapter.viewHolders.get(0).itemView;
        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        assertEquals(0, adapter.getSelections().size());
        assertFalse(checkBox.isChecked());
        adapter.setSelections(selection);
        adapter.notifyDataSetChanged();
        assertEquals(1, adapter.getSelections().size());
        assertTrue(checkBox.isChecked());
    }
}