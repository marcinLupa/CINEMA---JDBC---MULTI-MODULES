package model;

import exceptions.ExceptionCode;
import exceptions.MyException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LoyaltyCard {
    private Integer id;
    private String expirationDate;
    private BigDecimal discount;
    private Integer moviesNumberId;
    /**
     * class used to create an objects, with validation by builder pattern
     * @param builder pattern instance
     */
    private LoyaltyCard(LoyaltyCardBuilder builder) {
        this.id = builder.id;
        this.expirationDate = builder.expirationDate;
        this.discount = builder.discount;
        this.moviesNumberId = builder.moviesNumberId;
    }


    public static LoyaltyCardBuilder builder() {

        return new LoyaltyCardBuilder();
    }


    public static class LoyaltyCardBuilder {

        private Integer id;
        private String expirationDate;
        private BigDecimal discount;
        private Integer moviesNumberId;

        public LoyaltyCardBuilder id(Integer id) {
            try {

                if (id == null) {
                    throw new NullPointerException("ID IS NULL");
                }
                this.id = id;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER LOYALTY CARD BUILDER ID EXCEPTION: " + e.getMessage());
            }
        }
        public LoyaltyCardBuilder expirationDate(String expirationDate) {
            try {

                if (expirationDate == null) {
                    throw new NullPointerException("DATA IS NULL");
                }
                this.expirationDate = expirationDate;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER LOYALTY CARD BUILDER EXPIRATION DATE EXCEPTION: " + e.getMessage());
            }
        }
        public LoyaltyCardBuilder discount(BigDecimal discount) {
            try {

                if (discount == null) {
                    throw new NullPointerException("DISCOUNT IS NULL");
                }

                if (discount.compareTo(new BigDecimal(0)) < 0) {
                    throw new IllegalArgumentException("DISCOUNT IS UNDER 0");
                }

                this.discount = discount;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER LOYALTY CARD DISCOUNT EXCEPTION: " + e.getMessage());
            }
        }

        public LoyaltyCardBuilder moviesNumberId(Integer moviesNumberId) {
            try {

                if (moviesNumberId == null) {
                    throw new NullPointerException("ID IS NULL");
                }
                this.moviesNumberId = moviesNumberId;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER LOYALTY CARD BUILDER MOUVIES NUMBER_ID EXCEPTION: " + e.getMessage());
            }
        }

        public LoyaltyCard builder() {
            return new LoyaltyCard(this);
        }
    }

    @Override
    public String toString() {
        return "NUMER KARTY: "+ id +
                " ,DATA WAZNOSCI: " + expirationDate +
                " ,PRZYSLUGUJACA ZNIZKA: " + String.format("%2.2f",discount) +
                " zł, ILE FILMOW POZOSTALO DO OBEJRZENIA W RAMACH ZNIKI? " + moviesNumberId;
    }
}
