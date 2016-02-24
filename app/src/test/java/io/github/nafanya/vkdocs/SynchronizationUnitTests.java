package io.github.nafanya.vkdocs;

import org.junit.Before;
import org.junit.Test;

import io.github.nafanya.vkdocs.data.DocumentRepositoryImpl;
import io.github.nafanya.vkdocs.data.database.mapper.DbToDomainMapper;
import io.github.nafanya.vkdocs.data.database.repository.TestDatabaseRepository;
import io.github.nafanya.vkdocs.data.net.TestNetworkRepository;
import io.github.nafanya.vkdocs.domain.repository.DocumentRepository;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */

public class SynchronizationUnitTests {

    private DocumentRepository documentRepository;

    @Before
    public void setUp() {
        documentRepository = new DocumentRepositoryImpl(
                new TestDatabaseRepository(new DbToDomainMapper()),
                new TestNetworkRepository(100));
    }

    @Test
    public void syncTest() {

    }
}