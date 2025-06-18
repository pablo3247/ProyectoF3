import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ContratoStressTest extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://tu-servidor.com")
            .acceptHeader("application/json");

    ScenarioBuilder scn = scenario("Listar Contratos")
            .exec(http("listar_contratos")
                    .get("/root/contratos"));

    {
        setUp(
                scn.injectOpen(
                        rampUsers(100).during(10) // 100 usuarios en 10 segundos
                )
        ).protocols(httpProtocol);
    }
}