package seteat.stripetest;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private TextView cardNumber;
    private TextView month;
    private TextView year;
    private TextView cvc;
    private Button submitBtn;
    private TextView indicateLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardNumber = (TextView) findViewById(R.id.cardNumber);
        month = (TextView) findViewById(R.id.month);
        year = (TextView) findViewById(R.id.year);
        cvc = (TextView) findViewById(R.id.cvc);
        submitBtn = (Button) findViewById(R.id.submitButton);
        indicateLabel = (TextView) findViewById(R.id.indicateLabel);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCardInfo();
                indicateLabel.setText("Success!");
            }
        });

    }

    private void submitCardInfo() {
        Card card = new Card(
                cardNumber.getText().toString(),
                Integer.valueOf(month.getText().toString()),
                Integer.valueOf(year.getText().toString()),
                cvc.getText().toString()
        );

        if(card.validateCard()) {

            Context mContext = this;
            Stripe stripe = new Stripe(mContext, "pk_live_NvRP8UXHWMsgoIQAN3tw8xwE");

            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            //Token successfully created.
                            //Create a charge or save token to the server and use it later
                            startNewCharge(token);
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(getApplicationContext(),
                                    "ERROR ON CREATING TOKEN",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        } else {
            indicateLabel.setText("Card Invalid");
        }
    }


    public void startNewCharge(Token token) {

        String tokId = token.getId();

        final Map<String, Object> chargeParams = new HashMap<String, Object>();

        chargeParams.put("amount", 51);
        chargeParams.put("currency", "usd");
        chargeParams.put("description", "Real Card Example from Android");
        chargeParams.put("source", tokId);

        new AsyncTask<Void, Void, Void>() {

            Charge charge;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    com.stripe.Stripe.apiKey = "sk_live_6kkXDVVrX7edZeBZ2V1KfqIV";

                    charge = Charge.create(chargeParams);

                    Log.i("IsCharged", charge.getCreated().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Void result) {

                Toast.makeText(getApplicationContext(), "Card Charged : " + charge.getCreated() + "\nPaid : " +charge.getPaid(), Toast.LENGTH_LONG).show();
            };

        }.execute();

    }





}
