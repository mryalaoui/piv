AbattementTauxTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class AbattementTauxTest {

    @Test
    void testCreation() {
        AbattementTaux t = new AbattementTaux(new BigDecimal("0.06"), new BigDecimal("0.30"));
        assertEquals(new BigDecimal("0.06"), t.tauxAnnuel());
        assertEquals(new BigDecimal("0.30"), t.tauxAbattementGlobal());
    }
}

✅ AbattementYearTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class AbattementYearTest {

    @Test
    void testCreation() {
        AbattementTaux ir = new AbattementTaux(new BigDecimal("0.06"), new BigDecimal("0.30"));
        AbattementTaux ps = new AbattementTaux(new BigDecimal("0.016"), new BigDecimal("0.09"));
        AbattementYear a = new AbattementYear(ir, ps);
        assertEquals(ir, a.IR());
        assertEquals(ps, a.PS());
    }
}

✅ OrigineBienTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrigineBienTest {

    @Test
    void testEnumValues() {
        assertNotNull(OrigineBien.ACHAT);
        assertNotNull(OrigineBien.DONATION);
        assertNotNull(OrigineBien.SUCCESSION);
    }
}

✅ PviSimulationRequestTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PviSimulationRequestTest {

    @Test
    void testCreation() {
        PviSimulationRequest r = new PviSimulationRequest(
                LocalDate.of(2013, 3, 15),
                LocalDate.of(2025, 9, 10),
                new BigDecimal("150000"),
                new BigDecimal("0"),
                new BigDecimal("280000"),
                new BigDecimal("0"),
                TravauxFlag.NON,
                new BigDecimal("0")
        );

        assertEquals(LocalDate.of(2013, 3, 15), r.dateOrigine());
        assertEquals(new BigDecimal("150000"), r.prixOrigine());
        assertEquals(new BigDecimal("280000"), r.prixVente());
        assertEquals(TravauxFlag.NON, r.travaux());
    }
}

✅ SuggestionTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import static org.junit.jupiter.api.Assertions.*;

class SuggestionTest {

    @Test
    void testCreation() throws Exception {
        ObjectMapper m = new ObjectMapper();
        JsonNode n = m.readTree("{\"test\":1}");
        Suggestion s = new Suggestion("cat", "val", "url", n);
        assertEquals("cat", s.category());
        assertEquals("val", s.value());
        assertEquals("url", s.url());
        assertEquals(1, s.conditionNode().get("test").asInt());
    }
}

✅ PviSimulationResultTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PviSimulationResultTest {

    @Test
    void testCreation() {
        PviSimulationResult r = new PviSimulationResult(
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.ZERO,
                BigDecimal.ONE,
                BigDecimal.ZERO,
                BigDecimal.valueOf(5),
                null,
                null
        );

        assertEquals(BigDecimal.TEN, r.plusValueBrute());
        assertEquals(BigDecimal.ONE, r.plusValueNette());
        assertEquals(BigDecimal.ZERO, r.impotIR());
        assertEquals(BigDecimal.ONE, r.impotPS());
        assertEquals(BigDecimal.ZERO, r.surtaxe());
        assertEquals(BigDecimal.valueOf(5), r.netDisponible());
    }
}

✅ TaxePlusValueBracketTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class TaxePlusValueBracketTest {

    @Test
    void testCreation() {
        TaxePlusValueBracket b = new TaxePlusValueBracket(
                new BigDecimal("50000"),
                new BigDecimal("100000"),
                new BigDecimal("0.02"),
                new BigDecimal("0.10"),
                new BigDecimal("0.5")
        );

        assertEquals(new BigDecimal("50000"), b.min());
        assertEquals(new BigDecimal("100000"), b.max());
        assertEquals(new BigDecimal("0.02"), b.taux());
        assertEquals(new BigDecimal("0.10"), b.pourcentagePlusValue());
        assertEquals(new BigDecimal("0.5"), b.rapport());
    }
}

✅ TaxOutcomeStateTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaxOutcomeStateTest {

    @Test
    void testEnumValues() {
        assertNotNull(TaxOutcomeState.EXONERATION);
        assertNotNull(TaxOutcomeState.IMPOSABLE_IR_PS);
        assertNotNull(TaxOutcomeState.IMPOSABLE_IR_PS_SURTAXE);
    }
}

✅ TravauxFlagTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TravauxFlagTest {

    @Test
    void testValueOfString() {
        assertEquals(TravauxFlag.OUI, TravauxFlag.from("oui"));
        assertEquals(TravauxFlag.NON, TravauxFlag.from("non"));
    }
}

✅ TypeBienTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TypeBienTest {

    @Test
    void testEnumValues() {
        assertNotNull(TypeBien.APPARTEMENT);
        assertNotNull(TypeBien.MAISON);
        assertNotNull(TypeBien.TERRAIN);
    }
}

✅ UsageBienTest
package com.bnpparibas.dsibddf.fis.pvi.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsageBienTest {

    @Test
    void testEnumValues() {
        assertNotNull(UsageBien.RESIDENCE_PRINCIPALE);
        assertNotNull(UsageBien.RESIDENCE_SECONDAIRE);
        assertNotNull(UsageBien.LOCATION);
    }
}
