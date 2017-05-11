package pl.pltest.app.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import pl.pltest.app.R;

import java.io.*;

public class FileActivity extends AppCompatActivity {

    File externalStorage = Environment.getExternalStorageDirectory();
    String pathName = "pl.txt";
    String testString = "漂洋过海来看你";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        writerFileByOutputStream();
//        writerFileByBufferWriter();
//        readFileByInputStream();
        readFileByBufferReader();
    }

    private void writerFileByBufferWriter() {
        File file = new File(externalStorage,pathName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
            String bb = " 周华健";
            bw.write(bb);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writerFileByOutputStream() {
        File file = new File(externalStorage,pathName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(testString.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFileByBufferReader() {
        File file = new File(externalStorage,pathName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readLine = "";
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null){
                sb.append(readLine + "\r\n");
            }
            br.close();
            ((TextView)findViewById(R.id.textView)).setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFileByInputStream() {
        File file = new File(externalStorage,pathName);
        try {
            FileInputStream in = new FileInputStream(file);
            byte[] b = new byte[in.available()];
            in.read(b);
            String result = new String(b);
            ((TextView)findViewById(R.id.textView)).setText(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
