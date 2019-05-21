package model;

import exceptions.ExceptionCode;
import exceptions.MyException;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private Integer id;
    private String name;
    private String surname;
    private Integer age;
    private String email;
    private Integer loyaltyCardId;

    /**
     * class used to create an objects, with validation by builder pattern
     * @param builder pattern instance
     */
    private Customer(CustomerBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.surname = builder.surname;
        this.age = builder.age;
        this.email = builder.email;
        this.loyaltyCardId = builder.loyaltyCardId;
    }

    public static CustomerBuilder builder() {

        return new CustomerBuilder();
    }

    public static class CustomerBuilder {

        private Integer id;
        private String name;
        private String surname;
        private Integer age;
        private String email;
        private Integer loyaltyCardId;


        final private String REGEX = "^[a-zA-Z0-9]*$";
        final private String EMAIL_REGEX = "\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b";

        public CustomerBuilder id(Integer id) {
            try {

                if (id == null) {
                    throw new NullPointerException("ID IS NULL");
                }
                this.id = id;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER CUSTOMER BUILDER ID EXCEPTION: " + e.getMessage());
            }
        }

        public CustomerBuilder name(String name) {
            try {

                if (name == null) {
                    throw new NullPointerException("NAME IS NULL");
                }

                if (!name.matches(REGEX)) {
                    throw new IllegalArgumentException("NAME IS NOT CORRECT");
                }
                this.name = name;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER CUSTOMER BUILDER NAME EXCEPTION: " + e.getMessage());
            }
        }

        public CustomerBuilder surname(String surname) {
            try {

                if (surname == null) {
                    throw new NullPointerException("SURNAME IS NULL");
                }

                if (!surname.matches(REGEX)) {
                    throw new IllegalArgumentException("SURNAME IS NOT CORRECT");
                }
                this.surname = surname;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER CUSTOMER BUILDER SURNAME EXCEPTION: " + e.getMessage());
            }
        }

        public CustomerBuilder age(Integer age) {
            try {

                if (age == null) {
                    throw new NullPointerException("AGE IS NULL");
                }
                this.age = age;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER CUSTOMER BUILDER AGE EXCEPTION: " + e.getMessage());
            }
        }

        public CustomerBuilder email(String email) {
            try {

                if (email == null) {
                    throw new NullPointerException("EMAIL IS NULL");
                }

                if (!email.matches(EMAIL_REGEX)) {
                    throw new IllegalArgumentException("EMAIL IS NOT CORRECT");
                }
                this.email = email;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER CUSTOMER BUILDER EMAIL EXCEPTION: " + e.getMessage());
            }
        }

        public CustomerBuilder loyaltyCardId(Integer loyaltyCardId) {
            try {

                if (loyaltyCardId == null) {
                    throw new NullPointerException("LOYALTY CARD ID IS NULL");
                }
                this.loyaltyCardId = loyaltyCardId;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER CUSTOMER BUILDER LOYALTY CARD ID EXCEPTION: " + e.getMessage());
            }
        }

        public Customer builder() {
            return new Customer(this);
        }
    }

    @Override
    public String toString() {
        return " ID: " + id +", "+
                name +", "+
                surname  +
                ", WIEK: " + age  +", "+
                email  +
                ", NR KARTY STALEGO KLIENTA: " + loyaltyCardId;
    }
}
