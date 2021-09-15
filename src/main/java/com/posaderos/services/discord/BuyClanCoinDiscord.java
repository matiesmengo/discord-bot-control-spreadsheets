package com.posaderos.services.discord;

import com.posaderos.configuration.common.DiscordEventListener;
import com.posaderos.services.GoogleSheetsService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BuyClanCoinDiscord implements DiscordEventListener<MessageCreateEvent> {
    private final GoogleSheetsService googleSheetsService;

    @Autowired
    public BuyClanCoinDiscord(final GoogleSheetsService googleSheetsService) {
        this.googleSheetsService = googleSheetsService;
    }

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(final MessageCreateEvent event) {
        return Mono.just(event.getMessage())
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().contains("!enviarOro"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(getMessage(event)))
                .doOnNext(x -> this.googleSheetsService.buyClanCoins(getUserName(event), goldNumber(event)))
                .then();
    }

    private String getMessage(final MessageCreateEvent event) {
        return getUserId(event) + " ha solicitado intercambiar " + goldNumber(event) + " de oro por monedas de clan";
    }

    private String getUserId(final MessageCreateEvent event) {
        return event.getMessage().getAuthor().get().getMention();
    }

    private String getUserName(final MessageCreateEvent event) {
        return event.getMessage().getAuthor().get().getUsername()
                + "#"
                + event.getMessage().getAuthor().get().getDiscriminator();
    }

    private String goldNumber(final MessageCreateEvent event) {
        return event.getMessage().getContent().split(" ")[1];
    }
}
