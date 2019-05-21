package service;


import exceptions.ExceptionCode;
import exceptions.MyException;
import json.impl.CustomerListConverter;
import json.impl.LoyaltyCardListConverter;
import json.impl.MovieListConverter;
import json.impl.SalesStandListConverter;
import model.Customer;
import model.LoyaltyCard;
import model.Movie;
import model.SalesStand;
import repository.impl.CustomerRepository;
import repository.impl.LoyaltyCardRepository;
import repository.impl.MovieRepository;
import repository.impl.SalesStandRepository;

import java.util.List;

/**
 * @author Marcin Lupa
 * class used to initilise app by examplary data to tests
 */
 class DateBaseInitializationService {
    private final UserDataService userDataService = new UserDataService();

    void dbInitialization() {

        try {
            System.out.println("CZY JESTES PEWIEN ZE CHCESZ USUNAC DANE I ZAINICJOWAC BAZE?(TAK/NIE)");
            if (userDataService.getYesOrNo()) {

                new SalesStandRepository().deleteAll();
                new CustomerRepository().deleteAll();
                new LoyaltyCardRepository().deleteAll();
               new MovieRepository().deleteAll();
                final String CUSTOMER_LIST_FILENAME = "C:/Users/Pocisk/IdeaProjects/PROJEKTY/JDBC/CINEMA_MULTIMODULE/json/src/main/resources/files/toInitilizaton/CUSTOMERS.json";
                final String MOVIE_LIST_FILENAME = "C:/Users/Pocisk/IdeaProjects/PROJEKTY/JDBC/CINEMA_MULTIMODULE/json/src/main/resources/files/toInitilizaton/MOVIES.json";
                final String LOYALTY_CARD_LIST_FILENAME = "C:/Users/Pocisk/IdeaProjects/PROJEKTY/JDBC/CINEMA_MULTIMODULE/json/src/main/resources/files/toInitilizaton/LOYALTYCARD.json";
                final String SALES_STAND_LIST_FILENAME = "C:/Users/Pocisk/IdeaProjects/PROJEKTY/JDBC/CINEMA_MULTIMODULE/json/src/main/resources/files/toInitilizaton/SALESSTAND.json";

                CustomerListConverter customerListConverter = new CustomerListConverter(CUSTOMER_LIST_FILENAME);
                MovieListConverter movieListConverter = new MovieListConverter(MOVIE_LIST_FILENAME);
                LoyaltyCardListConverter loyaltyCardListConverter = new LoyaltyCardListConverter(LOYALTY_CARD_LIST_FILENAME);
                SalesStandListConverter salesStandListConverter = new SalesStandListConverter(SALES_STAND_LIST_FILENAME);

                System.out.println("WCZYTANE KARTY STALEGO KLIENTA:");
                List<LoyaltyCard> loyaltyCards = loyaltyCardListConverter
                        .fromJson()
                        .orElseThrow(() -> new MyException(ExceptionCode.SERVICE,"PARSING FROM JSON FILE EXCEPTION"));
                loyaltyCards.forEach(x -> new LoyaltyCardRepository().add(x));
                loyaltyCards.forEach(System.out::println);

                System.out.println("WCZYTANI UZYTKOWNICY:");
                List<Customer> customers =
                        customerListConverter
                                .fromJson()
                                .orElseThrow(() -> new MyException(ExceptionCode.SERVICE,"PARSING FROM JSON FILE EXCEPTION"));

                customers.forEach(x -> new CustomerRepository().add(x));
                customers.forEach(System.out::println);

                System.out.println("WCZYTANE FILMY:");
                List<Movie> movies =
                        movieListConverter
                                .fromJson()
                                .orElseThrow(() -> new MyException(ExceptionCode.SERVICE,"PARSING FROM JSON FILE EXCEPTION"));
                movies.forEach(x -> new MovieRepository().add(x));
                movies.forEach(System.out::println);

                System.out.println("HISTORIA TRANSAKCJI WSZYSTKICH KLIENTOW:");
                List<SalesStand> salesStands = salesStandListConverter
                        .fromJson()
                        .orElseThrow(() -> new MyException(ExceptionCode.SERVICE,"PARSING FROM JSON FILE EXCEPTION"));

                salesStands.forEach(x -> new SalesStandRepository().add(x));
                salesStands.forEach(System.out::println);

                //USTAWIAM ODPOWIEDNIE WARTOSCI DLA KART STALEGO KLIENTA
                customers.stream()
                        .filter(x -> x.getLoyaltyCardId() > 0)
                        .forEach(x -> new CustomerRepository().updateAutomatic(x));
            } else {
                System.out.println("POWROT DO MENU GLOWNEGO");
            }

        } catch (MyException e) {

            throw new MyException(ExceptionCode.SERVICE, "JSON FILE INITIALIZATION EXCEPTION" + e.getMessage());
        }
    }
}
