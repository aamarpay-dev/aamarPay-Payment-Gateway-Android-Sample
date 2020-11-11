package com.aamarpay.aamarpayandroidsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aamarpay.library.AamarPay;
import com.aamarpay.library.DialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private AamarPay aamarPay;
    private DialogBuilder dialogBuilder;
    private String trxID, trxAmount, trxCurrency, customerName, customerEmail, customerPhone, customerAddress, customerCity, customerCountry, paymentDescription;
    EditText trx_id, trx_amount, trx_currency, customer_name, customer_email, customer_phone, customer_address, customer_city, customer_country, payment_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init dialog builder
        dialogBuilder = new DialogBuilder(MainActivity.this, alertDialog);

        // Private Dialog
        builder = new AlertDialog.Builder(this);

        // Reference to fields
        trx_id = findViewById(R.id.trx_id);
        trx_amount = findViewById(R.id.trx_amount);
        trx_currency = findViewById(R.id.trx_currency);
        customer_name = findViewById(R.id.customer_name);
        customer_email = findViewById(R.id.customer_email);
        customer_phone = findViewById(R.id.customer_phone);
        customer_address = findViewById(R.id.customer_address);
        customer_city = findViewById(R.id.customer_city);
        customer_country = findViewById(R.id.customer_country);
        payment_description = findViewById(R.id.payment_description);

        // Disabling Trx field edit
        trx_id.setFocusable(false);

        // Initiate payment
        aamarPay = new AamarPay(MainActivity.this, "aamarpay", "28c78bb1f45112f5d40b956fe104645a");

        // Set Test Mode
        aamarPay.testMode(true);

        // Auto generate Trx
        aamarPay.autoGenerateTransactionID(true);

        // Generate unique transaction id
        trxID = aamarPay.generate_trx_id();

        // Setting the values to fields
        trx_id.setText(trxID);

        // Get the data
        trxAmount = trx_amount.getText().toString();
        trxCurrency = trx_currency.getText().toString();
        customerName = customer_name.getText().toString();
        customerEmail = customer_email.getText().toString();
        customerPhone = customer_phone.getText().toString();
        customerAddress = customer_address.getText().toString();
        customerCity = customer_city.getText().toString();
        customerCountry = customer_country.getText().toString();
        paymentDescription = payment_description.getText().toString();

        // Setup pay now button
        Button payNow = findViewById(R.id.payButton);

        // Pay now on click
        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.showLoading();

                // Set transaction parameter
                aamarPay.setTransactionParameter(trxAmount, trxCurrency, paymentDescription);

                // Set Customer details
                aamarPay.setCustomerDetails(customerName, customerEmail, customerPhone, customerAddress, customerCity, customerCountry);

                // Initiating PGW
                aamarPay.initPGW(new AamarPay.onInitListener() {
                    @Override
                    public void onInitFailure(Boolean error, String message) {
                        // You will get the response, if payment gateway initialization is failed.
                        Log.d("TEST_IF", message);
                        dialogBuilder.dismissDialog();
                        dialogBuilder.errorPopUp(message);
                    }

                    @Override
                    public void onPaymentSuccess(JSONObject jsonObject) {
                        // You will get the payment success response as a JSON callback
                        Log.d("TEST_PS", jsonObject.toString());
                        dialogBuilder.dismissDialog();

                        builder.setTitle("Payment Success Response");
                        builder.setMessage(jsonObject.toString());

                        AlertDialog alertDialog1 = builder.create();
                        alertDialog1.show();
                    }

                    @Override
                    public void onPaymentFailure(JSONObject jsonObject) {
                        // You will get the payment failed response as a JSON callback
                        Log.d("TEST_PF", jsonObject.toString());
                        dialogBuilder.dismissDialog();

                        builder.setTitle("Payment Failed Response");
                        builder.setMessage(jsonObject.toString());

                        AlertDialog alertDialog1 = builder.create();
                        alertDialog1.show();
                    }

                    @Override
                    public void onPaymentProcessingFailed(JSONObject jsonObject) {
                        // You will get the payment processing failed response as a JSON callback
                        Log.d("TEST_PPF", jsonObject.toString());
                        dialogBuilder.dismissDialog();

                        builder.setTitle("Payment Processing Failed Response");
                        builder.setMessage(jsonObject.toString());

                        AlertDialog alertDialog1 = builder.create();
                        alertDialog1.show();
                    }

                    @Override
                    public void onPaymentCancel(JSONObject jsonObject) {
                        // You will get the payment cancel response as a JSON callback
                        Log.d("TEST_PC", jsonObject.toString());
                        try {
                            // Call the transaction verification check validity
                            aamarPay.getTransactionInfo(jsonObject.getString("trx_id"), new AamarPay.TransactionInfoListener() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
                                    Log.d("TEST_", jsonObject.toString());
                                    dialogBuilder.dismissDialog();

                                    builder.setTitle("Trx Verification Success Response");
                                    builder.setMessage(jsonObject.toString());

                                    AlertDialog alertDialog1 = builder.create();
                                    alertDialog1.show();
                                }

                                @Override
                                public void onFailure(Boolean error, String message) {
                                    Log.d("TEST_", message);
                                    dialogBuilder.dismissDialog();
                                    dialogBuilder.errorPopUp(message);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        // Swipe refresh to re generate trx id
        swipeRefresh();

        // Text watcher for monitoring change
        trx_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                trxAmount = s.toString();
                payNow.setText(String.format("Pay %s %s", trxCurrency.toUpperCase(), s.toString()));
            }
        });

        trx_currency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                trxCurrency = s.toString();
                payNow.setText(String.format("Pay %s %s", s.toString().toUpperCase(), trxAmount));
            }
        });
    }

    // Swipe refresh layout actions
    private void swipeRefresh() {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Regenerate unique transaction id
                trxID = aamarPay.generate_trx_id();

                // Setting the values to fields
                trx_id.setText(trxID);

                // Set refreshing false
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}