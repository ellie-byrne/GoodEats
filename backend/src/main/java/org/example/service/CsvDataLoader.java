package org.example.service;

import jakarta.annotation.PostConstruct;
import org.example.model.Restaurant;
import org.example.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(CsvDataLoader.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @PostConstruct
    public void loadDataFromCsv() {
        logger.info("Starting to load restaurant data from CSV");

        // Only load data if the database is empty
        if (restaurantRepository.count() > 0) {
            logger.info("Database already contains data, skipping CSV import");
            return;
        }

        try {
            String csvUrl = "https://hebbkx1anhila5yf.public.blob.vercel-storage.com/5%20restaurants%20-%20Sheet1-92mmFnF0BQEwH65vyECSSKpYz0UyP0.csv";
            URL url = new URL(csvUrl);
            List<Restaurant> restaurants = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {

                // Skip header line
                String line = br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] data = parseCsvLine(line);

                    if (data.length >= 3) {
                        Restaurant restaurant = new Restaurant(
                                data[1].trim(),  // name
                                data[2].trim(),  // category
                                data[0].trim(),  // borough
                                "/placeholder.svg?height=300&width=400&text=" + data[1].trim(), // storePhoto
                                "" // link (empty for now)
                        );
                        restaurants.add(restaurant);
                    }
                }
            }

            restaurantRepository.saveAll(restaurants);
            logger.info("Successfully loaded {} restaurants from CSV", restaurants.size());

        } catch (Exception e) {
            logger.error("Error loading CSV data: {}", e.getMessage(), e);
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }

        result.add(field.toString());
        return result.toArray(new String[0]);
    }
}


