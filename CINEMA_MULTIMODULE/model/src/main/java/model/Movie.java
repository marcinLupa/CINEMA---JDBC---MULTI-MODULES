package model;

import exceptions.ExceptionCode;
import exceptions.MyException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Movie {
    private Integer id;
    private String title;
    private String genre;
    private BigDecimal price;
    private Integer duration;
    private String releaseDate;
    /**
     * class used to create an objects, with validation by builder pattern
     * @param builder pattern instance
     */
    private Movie(MovieBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.genre = builder.genre;
        this.price = builder.price;
        this.duration = builder.duration;
        this.releaseDate = builder.releaseDate;
    }


    public static MovieBuilder builder() {

        return new MovieBuilder();
    }

    public static class MovieBuilder {

        private Integer id;
        private String title;
        private String genre;
        private BigDecimal price;
        private Integer duration;
        private String releaseDate;


        final private String REGEX = "[A-Za-z0-9 ]*";

        public MovieBuilder id(Integer id) {
            try {

                if (id == null) {
                    throw new NullPointerException("ID IS NULL");
                }
                this.id = id;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER MOVIE BUILDER ID EXCEPTION: " + e.getMessage());
            }
        }

        public MovieBuilder title(String title) {
            try {

                if (title == null) {
                    throw new NullPointerException("TITLE IS NULL");
                }

                if (!title.matches(REGEX)) {
                    throw new IllegalArgumentException("TITLE IS NOT CORRECT");
                }

                this.title = title;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER MOVIE BUILDER TITLE EXCEPTION: " + e.getMessage());
            }
        }

        public MovieBuilder genre(String genre) {
            try {

                if (genre == null) {
                    throw new NullPointerException("GENRE IS NULL");
                }

                if (!genre.matches(REGEX)) {
                    throw new IllegalArgumentException("GENRE IS NOT CORRECT");
                }

                this.genre = genre;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER MOVIE BUILDER GENRE EXCEPTION: " + e.getMessage());
            }
        }

        public MovieBuilder price(BigDecimal price) {
            try {

                if (price == null) {
                    throw new NullPointerException("PRICE IS NULL");
                }

                if (price.compareTo(new BigDecimal(0)) < 0) {
                    throw new IllegalArgumentException("PRICE IS UNDER 0");
                }

                this.price = price;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER MOVIE BUILDER PRICE EXCEPTION: " + e.getMessage());
            }
        }

        public MovieBuilder duration(Integer duration) {
            try {

                if (duration == null) {
                    throw new NullPointerException("DURATION IS NULL");
                }
                this.duration = duration;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER MOVIE BUILDER DURATION EXCEPTION: " + e.getMessage());
            }
        }

        public MovieBuilder releaseDate(String releaseDate) {
            try {

                if (releaseDate == null) {
                    throw new NullPointerException("ID IS NULL");
                }

                this.releaseDate = releaseDate;
                return this;

            } catch (Exception e) {
                throw new MyException(ExceptionCode.BUILDER, "BUILDER MOVIE BUILDER BUILDER RELASE DATE EXCEPTION: " + e.getMessage());
            }
        }

        public Movie builder() {
            return new Movie(this);
        }
    }

    @Override
    public String toString() {
        return " NUMER W TABELI: " + id +
                ", TYTUL: " + title +
                ", GATUNEK: " + genre +
                ", CENA: " + String.format("%2.2f", price) +
                ", CZAS TRWANIA FILMU: " + duration +
                ", DATA PREMIERY FILMU: " + releaseDate;
    }



}
