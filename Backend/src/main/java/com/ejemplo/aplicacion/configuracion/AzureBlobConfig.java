package com.ejemplo.aplicacion.configuracion;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobConfig {

    @Bean
    public BlobContainerClient blobContainerClient() {
        String accountName = "devstoreaccount1";
        String accountKey = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);

        String endpoint = "http://127.0.0.1:10000/" + accountName;

        String containerName = "contratos";

        return new BlobContainerClientBuilder()
                .endpoint(endpoint)
                .credential(credential)
                .containerName(containerName)
                .buildClient();
    }
}
