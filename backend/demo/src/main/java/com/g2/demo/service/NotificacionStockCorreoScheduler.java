package com.g2.demo.service;

import com.g2.demo.entity.NotificacionStock;
import com.g2.demo.repository.NotificacionStockRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "app.notifications.stock.scheduler.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class NotificacionStockCorreoScheduler {

    private final NotificacionStockRepository repository;
    private final NotificacionStockCorreoService correoService;

    public NotificacionStockCorreoScheduler(NotificacionStockRepository repository,
                                            NotificacionStockCorreoService correoService) {
        this.repository = repository;
        this.correoService = correoService;
    }

    @Scheduled(
            initialDelayString = "${app.notifications.stock.initial-delay-ms:60000}",
            fixedDelayString = "${app.notifications.stock.reintento-ms:15000}"
    )
    public void enviarPendientes() {
        repository.findTop10ByTipoAndResueltaFalseAndEnviadoFalseOrderByFechaEnvioAsc(
                        NotificacionStockService.TIPO_STOCK_CRITICO)
                .stream()
                .map(NotificacionStock::getId)
                .forEach(correoService::enviarCorreo);
    }
}
