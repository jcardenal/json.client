package es.jcardenal.examples.json.client.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@NoArgsConstructor
@Data
public class SimpleProductDTO {
    private String ean;
    private String description;

    @JsonAlias("firstPrice")
    private SimplePriceDTO tagPrice;
}
