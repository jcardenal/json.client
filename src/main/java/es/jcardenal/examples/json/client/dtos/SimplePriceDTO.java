package es.jcardenal.examples.json.client.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SimplePriceDTO {
    private String price;
    private String currency;
}
