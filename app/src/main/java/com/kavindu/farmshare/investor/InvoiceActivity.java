package com.kavindu.farmshare.investor;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kavindu.farmshare.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

public class InvoiceActivity extends AppCompatActivity {

    String id;
    String date;
    String farmName;
    String stockCount;
    String price;

    File invoiceFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        farmName = intent.getStringExtra("farmName");
        stockCount = intent.getStringExtra("stockCount");
        price = "Rs." +intent.getStringExtra("price");

        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        date = formatter.format(currentDate);

        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        id = "#"+String.valueOf(randomNumber);

        TextView textViewId = findViewById(R.id.textView145);
        TextView textViewDate = findViewById(R.id.textView146);
        TextView textViewName = findViewById(R.id.textView147);
        TextView textViewStock = findViewById(R.id.textView148);
        TextView textViewPrice = findViewById(R.id.textView149);

        textViewId.setText(id);
        textViewDate.setText(date);
        textViewName.setText(farmName);
        textViewStock.setText(stockCount);
        textViewPrice.setText(price);

        generateInvoicePdf();

        Button printButton = findViewById(R.id.button);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPdf();

            }
        });

    }

    private void generateInvoicePdf() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);

        // Create PDF page (A4 size: 595 x 842)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int startX = 50; // Left margin
        int startY = 50; // Top margin
        int lineSpacing = 30; // Space between lines
        int tableStartY = 220; // Start position of the table

        // ** Draw Invoice Header **
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText("INVOICE", 240, startY, paint);

        // ** Order Details **
        paint.setTextSize(16);
        paint.setFakeBoldText(false);
        startY += 50;
        canvas.drawText("Order ID: " + id, startX, startY, paint);
        startY += lineSpacing;
        canvas.drawText("Total Amount: Rs." + price+"0", startX, startY, paint);
        startY += lineSpacing;
        canvas.drawText("DATE:" + date, startX, startY, paint);
        startY += lineSpacing;

        // ** Draw Table Header **
        paint.setTextSize(14);
        paint.setFakeBoldText(true);
        canvas.drawText("Item", startX, tableStartY, paint);
        canvas.drawText("Qty", startX + 250, tableStartY, paint);
        canvas.drawText("Price", startX + 350, tableStartY, paint);

        // Draw Line under Header
        canvas.drawLine(startX, tableStartY + 10, 540, tableStartY + 10, linePaint);

        // ** Draw Cart Items **
        paint.setFakeBoldText(false);
        int itemStartY = tableStartY + 40; // First row position

            canvas.drawText(farmName, startX, itemStartY, paint);
            canvas.drawText(String.valueOf(stockCount), startX + 250, itemStartY, paint);
            canvas.drawText(price, startX + 450, itemStartY, paint);
            itemStartY += lineSpacing;


        // ** Draw Bottom Line **
        canvas.drawLine(startX, itemStartY, 540, itemStartY, linePaint);
        itemStartY += 40;

        // ** Draw Thank You Message **
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        canvas.drawText("Thank you for your purchase!", startX, itemStartY, paint);

        // Finish PDF page
        pdfDocument.finishPage(page);

        // Save PDF file
        invoiceFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "invoice_" + id + ".pdf");
        try {
            FileOutputStream fos = new FileOutputStream(invoiceFile);
            pdfDocument.writeTo(fos);
            fos.close();
            pdfDocument.close();
            Toast.makeText(this, "Invoice saved: " + invoiceFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FarmShareLog", "Failed to get URI for file: " + e.getMessage());
        }
    }

    private void openPdf() {
        if (invoiceFile.exists()) {
            Log.i("FarmShareLog","line1");
            Uri pdfUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", invoiceFile);
            Log.i("FarmShareLog","line2");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Log.i("FarmShareLog","line3");
            intent.setDataAndType(pdfUri, "application/pdf");
            Log.i("FarmShareLog","line4");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.i("FarmShareLog","line5");
            startActivity(intent);
            Log.i("FarmShareLog","line6");
        } else {
            Toast.makeText(this, "Invoice not found!", Toast.LENGTH_SHORT).show();
        }
    }
}