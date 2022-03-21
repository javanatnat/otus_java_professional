package ru.otus.antibruteforce.config;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.core.convert.converter.Converter;
import ru.otus.antibruteforce.model.Ipv4Type;

import java.sql.SQLException;
import java.util.List;

@Configuration
public class DataJdbcConfig extends AbstractJdbcConfiguration {

    @Override
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(List.of(new WriteInetConverter(), new ReadInetConverter()));
    }

    @WritingConverter
    public static class WriteInetConverter implements Converter<Ipv4Type, PGobject> {
        private static final Logger LOG = LoggerFactory.getLogger(WriteInetConverter.class);

        @Override
        public PGobject convert(Ipv4Type source) {
            LOG.debug("convert: {}", source);

            PGobject pgobject = new PGobject();
            pgobject.setType("inet");
            try {
                pgobject.setValue(source.getValue());
            } catch (SQLException e) {
                LOG.debug("convert: error: {}, {}", e.getSQLState(), e.getMessage());
                throw new RuntimeException(e);
            }
            LOG.debug("convert: SUCCESS");

            return pgobject;
        }
    }

    @ReadingConverter
    public static class ReadInetConverter implements Converter<PGobject, Ipv4Type> {
        private static final Logger LOG = LoggerFactory.getLogger(ReadInetConverter.class);

        @Override
        public Ipv4Type convert(PGobject source) {
            LOG.debug("convert: {}", source.getValue());
            return new Ipv4Type(source.getValue());
        }
    }
}
