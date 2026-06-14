package com.rustamg.calc.loan;

import com.google.gson.annotations.SerializedName;
import com.rustamg.calc.loan.utils.GsonFactory;
import com.zoom.loancalc.Loan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by rustamg on 29/07/15.
 */
public class CalculationTestModel {

    @SerializedName("loan")
    private Loan mLoan;

    @SerializedName("consider_days_off")
    private boolean mConsiderDaysOff;

    @SerializedName("pay_on_last_day_of_month")
    private boolean mPayOnLastDayOfMonth;

    @SerializedName("description")
    private String mDescription;

    public CalculationTestModel(Loan loan, boolean considerDaysOff, boolean payOnLastDayOfMonth) {

        mLoan = loan;
        mConsiderDaysOff = considerDaysOff;
        mPayOnLastDayOfMonth = payOnLastDayOfMonth;
    }

    public Loan getLoan() {

        return mLoan;
    }

    public boolean isConsiderDaysOff() {

        return mConsiderDaysOff;
    }

    public boolean isPayOnLastDayOfMonth() {

        return mPayOnLastDayOfMonth;
    }

    public String getDescription() {

        return mDescription;
    }

    public static CalculationTestModel readFromFile(String fileName) {

        File file = getFileFromPath(fileName);

        return GsonFactory.create().fromJson(readFile(file), CalculationTestModel.class);
    }

    private static File getFileFromPath(String fileName) {
        //fileName = "c:/Source_android/loancalc/calculation/src/test/res/cases/annuity/fail_infinite_loan_extras_first.json";
        ClassLoader classLoader = CalculationTestModel.class.getClassLoader();
        //URL resource = classLoader.getResource(fileName);
        //System.out.println("File Found : " + resource.getPath());
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is: " + s);
        String path = s + "/src/test/" + fileName;
        return new File(path); //resource.getPath());
    }

    private static String readFile(File file) {

        StringBuilder content = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;

            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       // System.out.println(content.toString());
        return content.toString();
    }
}
