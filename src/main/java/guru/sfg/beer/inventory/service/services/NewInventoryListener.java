package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.events.NewInventoryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class NewInventoryListener {
    private final BeerInventoryRepository beerInventoryRepository;

    private final JmsTemplate jmsTemplate;

    public NewInventoryListener(BeerInventoryRepository beerInventoryRepository, JmsTemplate jmsTemplate) {
        this.beerInventoryRepository = beerInventoryRepository;
        this.jmsTemplate = jmsTemplate;
    }

    @Transactional
    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event){
        BeerDto beerDto = event.getBeerDto();
        log.debug("Got inventory: "+ event.toString());

        BeerInventory beerInventory = BeerInventory.builder()
                .beerId(beerDto.getId())
                .quantityOnHand(beerDto.getQuantityOnHand())
                .upc(beerDto.getUpc())
                .build();

        beerInventoryRepository.save(beerInventory);
        log.debug("saved new inventory: "+beerInventory.toString());
    }
}
