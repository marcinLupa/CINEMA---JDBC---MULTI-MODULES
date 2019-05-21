package model;

import exceptions.ExceptionCode;
import exceptions.MyException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SalesStand {
    private Integer id;
    private Integer moviesId;
    private Integer customerId;
    private LocalDateTime startDateTime;
    /**
     * class used to create an objects, with validation by builder pattern
     * @param builder pattern instance
     */
    private SalesStand(SalesStandBuilder builder) {
        this.id = builder.id;
        this.moviesId = builder.moviesId;
        this.customerId = builder.customerId;
        this.startDateTime = builder.startDateTime;
    }
    public static SalesStandBuilder builder() {


        return new SalesStandBuilder();
    }

    public static class SalesStandBuilder {
        private Integer id;
        private Integer moviesId;
        private Integer customerId;
        private LocalDateTime startDateTime;



        public SalesStandBuilder id(Integer id) throws MyException {
            try {

                if (id == null) {
                    throw new NullPointerException("ID IS NULL");
                }
                this.id = id;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SALES STAND BUILDER ID EXCEPTION: " + e.getMessage());
            }
        }

        public SalesStandBuilder moviesId(Integer moviesId) {
            try {

                if (moviesId == null) {
                    throw new NullPointerException("ID IS NULL");
                }
                this.moviesId = moviesId;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SALES STAND BUILDER MOVIES ID EXCEPTION: " + e.getMessage());
            }
        }
        public SalesStandBuilder customerId(Integer customerId) {
            try {

                if (customerId == null) {
                    throw new NullPointerException("ID IS NULL");
                }
                this.customerId = customerId;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SALES STAND BUILDER CUSTOMER ID EXCEPTION: " + e.getMessage());
            }
        }
        public SalesStandBuilder startDateTime(LocalDateTime startDateTime) {
            try {

                if (startDateTime == null) {
                    throw new NullPointerException("DATA IS NULL");
                }
                this.startDateTime = startDateTime;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER SALES STAND BUILDER START DATE TIME EXCEPTION: " + e.getMessage());
            }
        }


        public SalesStand builder() {

            return new SalesStand(this);
        }
    }

    @Override
    public String toString() {
        return
                " ID SPRZEDAZY: " + id +
                ", ID FILMU: " + moviesId +
                ", ID UZYTKOWNIKA" + customerId +
                ", DATA SEANSU: " + startDateTime;
    }
}
