package com.example.soa.driver;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/drivers")
public class DriverController {
 @Value("${server.port}") private String port;
 @Value("${eureka.instance.instance-id:driver-instance}") private String instanceId;
 private final List<Map<String,Object>> drivers = List.of(
  Map.of("id", 101, "name", "Arjun", "vehicle", "Car"),
  Map.of("id", 102, "name", "Priya", "vehicle", "Bike"),
  Map.of("id", 103, "name", "Rahul", "vehicle", "Van"));
 @GetMapping public Map<String,Object> all() {
  return Map.of("service", "DRIVER-SERVICE", "instanceId", instanceId, "port", port, "drivers", drivers);
 }
 @GetMapping("/{id}") public Map<String,Object> byId(@PathVariable int id) {
  return drivers.stream().filter(d -> ((Integer)d.get("id")) == id).findFirst()
   .map(d -> Map.<String,Object>of("instanceId", instanceId, "port", port, "driver", d))
   .orElseGet(() -> Map.of("error", "Driver not found", "id", id, "instanceId", instanceId, "port", port));
 }
}
