package hm.test.work.aop;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 
 * Aspecto que realiza un corte a los métodos en cierto paquete y, 
 * cuyo parámetro sea de tipo: 'HttpServletRequest'.
 * @author legolas
 * @version 1.0
 * 15/09/2020
 */
@Aspect
@Component
public class HeaderAspect {

	/** Mapa que contiene headers recuperados y sus valores asociados. */
	private Map<String, List<String>> headers = new HashMap<>();
	
	/**
	 * En tiempo de ejecución, para aquellas clases en el paquete: 'hm.test.work.front'
	 * y que tienen como argumento un objeto de tipo: 'HttpServletRequest', 
	 * se intercepta el método, antes de su ejecución: Para generar un mapa conteniendo la
	 * lista de valores asociados a un Header.   
	 * @param request
	 * @throws Throwable
	 */
	@Before("execution(* hm.test.work.front.*.*(..)) && args(request)")
	public Map<String, List<String>> getHeaders(HttpServletRequest request) throws Throwable {

		Map<String, List<String>> headers = new HashMap<>();
		
		//TODO Por evaluar, si el siguiente código tiene mejor desempeño 
		//	que si se construye una Lambda a partir: enumHeader 
		
		Enumeration<String> enumHeader = request.getHeaderNames();
		while (enumHeader.hasMoreElements()) {
			
			String header = enumHeader.nextElement();
			List<String> listHeaderbyName = Collections.list(request.getHeaders(header));
			headers.put(header, listHeaderbyName);
		}
		
		return headers;
	}
	
	
	/**
	 * En tiempo de ejecución, para aquellas clases en el paquete: 'hm.test.work.front'
	 * y que tienen como argumento un objeto de tipo: 'HttpServletRequest'y 'List<String>', 
	 * se intercepta el método, antes de su ejecución: Para generar un mapa conteniendo la
	 * lista de valores asociados a un Header.   
	 * @param request
	 * @param headerNames
	 * @throws Throwable
	 */
	@Before("execution(* hm.test.work.front.*.*(..)) && args(request,headerNames)")
	public Map<String, List<String>> getHeadersByFilter(
			HttpServletRequest request, List<String> headerNames) throws Throwable {

		Map<String, List<String>> headers = new HashMap<>();
		
		// Creamos filtro a usar
		Predicate<String> filter = 
				userFilter -> headerNames.contains(userFilter);
		
		// Lista de headers del request
		List<String> listHeader = Collections.list(request.getHeaderNames());
		
		// Filtramos y agregamos al mapa
		listHeader.stream().filter(filter).forEach(header -> {
			headers.put(header, Collections.list(request.getHeaders(header)));
		});
		
		return headers;
	}
}
