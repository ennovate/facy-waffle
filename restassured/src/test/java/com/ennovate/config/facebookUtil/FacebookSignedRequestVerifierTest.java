package com.ennovate.config.facebookUtil;

import com.ennovate.util.TimeSource;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FacebookSignedRequestVerifierTest {


    FacebookSignedRequestVerifier facebookSignedRequestVerifier;
    private static final String FB_SIGNED_REQUEST = "LR7y4KScaKNi9OALNhUcrxrP9ke6bQmeOcY-O4Oq_do.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImNvZGUiOiJBUUFjQzNQMmQ5VWhpWEtEWjhUc2VyNHJpb3lsNkkwcnRtUmRJN0t2ZDYzM1ItcGpSaFZILUxlLXJjZ0ljOGRfVHhsQ0Q4aDhMZDk0OU42XzBqRkFMRFBVNUFoeFp1QkUzT04wQlBBNUJHaHdkb04xM2tyN3EtWVJjNkN3YnJpRG8za2JwM1lzaElpTzl3Z1BTY0l2S2tpazVpRGpDRTBGbGdmWHA0TFJQdFNyOThZM3FwamlUczJlZ3V6U1lKNmtBNEdIOGFoRnNhZXJNWlhET25HaDdhcVBuTkY0bjY2RG5EU3VDNW93RVNjdGhfRHlsY3VaV3hDZ25rbEs0LThaNHh1a3dYYmptdzBadkdwNG96eFowclpvbmVNVTk1ZVdOZUs3cE9nYnFhdDdjQ3hsYVZOMW54a2JrZElDbGtIQVBfcks5amdjOEZqYWRiNkwwWm5TOVk5MSIsImlzc3VlZF9hdCI6MTQ1Nzg3NzI0MywidXNlcl9pZCI6IjE2ODUyMDg1OTg0MzM3OTAifQ";
    private static final String USER_ID = "1685208598433790";
    private TimeSource timeSource;

    @Before
    public void setUp() throws Exception {
        timeSource = mock(TimeSource.class);
        facebookSignedRequestVerifier = new FacebookSignedRequestVerifier("33b17e044ee6a4fa383f46ec6e28ea1d", timeSource);
    }

    @Test
    public void verifiesSignature_returnsTrue_forNonExpiredSignature() throws Exception {

        ZonedDateTime january2016 = ZonedDateTime.of(LocalDateTime.of(2016,1,1,0,0,0),
                ZoneId.of(TimeSource.ZONE_ID_UTC));
        when(timeSource.nowWithUTC()).thenReturn(january2016);
        boolean isValidSignature = facebookSignedRequestVerifier.verify(FB_SIGNED_REQUEST);
        assertThat(isValidSignature, is(true));
        assertThat(facebookSignedRequestVerifier.getUserId(), is(USER_ID));
    }


    @Test
    public void verifier_returnsFalse_forExpiredSignature() throws Exception {
        ZonedDateTime january2017 = ZonedDateTime.of(LocalDateTime.of(2017,1,1,0,0,0),
                ZoneId.of(TimeSource.ZONE_ID_UTC));
        when(timeSource.nowWithUTC()).thenReturn(january2017);
        boolean isValidSignature = facebookSignedRequestVerifier.verify(FB_SIGNED_REQUEST);
        assertThat(isValidSignature, is(false));

    }

    @Test
    public void verifier_returnsFalse_forUnSupportedEncoding() throws Exception {
        assertFalse(facebookSignedRequestVerifier.verify("LR7y4KScaKNi9OALNhUcrxrP9ke6bQmeOcY-O4Oq_do.LR7y4KScaKNi9OALNhUcrxrP9ke6bQmeOcY-O4Oq_do"));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void invalidSignatureStructure_throwsException() throws Exception{
        facebookSignedRequestVerifier.verify("LR7y4KScaKNi9OALNhUcrxrP9ke6bQmeOcY-O4Oq_do");
    }
}