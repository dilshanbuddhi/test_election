package lk.ijse.Utill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


    public class Regex {
        public static boolean isTextFieldValid(TextField textField, String text){
            String filed = "";

            switch (textField){
                case ID:
                    filed = "^([A-Z][0-9]{3})$";
                    break;
                case NAME:
                    filed = "^[A-z|\\\\s]{4,}$";
                    break;
                case EMAIL:
                    filed = "^([A-z])([A-z0-9.]){1,}[@]([A-z0-9]){1,10}[.]([A-z]){2,5}$";
                    break;
                case NIC:
                    filed ="^([0-9]{9}[x|X|v|V]|[0-9]{12})$";
                    break;
                case AGE:
                    filed = "^(?:1[01][0-9]|120|1[7-9]|[2-9][0-9])$";
                    break;
                case FULLNAME:
                    filed ="(^[A-Za-z]{3,16})([ ]{0,1})([A-Za-z]{2,16})?([ ]{0,1})?([A-Za-z]{3,16})?([ ]{0,1})?([A-Za-z]{3,16})";
                    break;
                case EID:
                    filed ="^E\\d+$";
                    break;
                case PID:
                    filed = "^P\\d+$";

            }

            Pattern pattern = Pattern.compile(filed);

            if (text != null){
                if (text.trim().isEmpty()){
                    return false;
                }
            }else {
                return false;
            }

            Matcher matcher = pattern.matcher(text);

            if (matcher.matches()){
                return true;
            }
            return false;
        }

        public static boolean setTextColor(TextField location, javafx.scene.control.TextField textField){
            if (Regex.isTextFieldValid(location, textField.getText())){
                textField.setStyle("-fx-text-box-border: green; -fx-text-inner-color: green;");

                return true;
            }else {
                textField.setStyle("-fx-text-box-border: red; -fx-text-inner-color: red;");

                return false;
            }
        }
    }


