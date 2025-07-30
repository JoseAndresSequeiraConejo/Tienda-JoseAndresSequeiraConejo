package com.tienda;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    // —— Internacionalización ——
    @Bean
    public LocaleResolver localeResolver() {
        var slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.getDefault());
        slr.setLocaleAttributeName("session.current.locale");
        slr.setTimeZoneAttributeName("session.current.timezone");
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean("messageSource")
    public MessageSource messageSource() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    // —— Mapeo directo a vistas estáticas ——
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/registro/nuevo").setViewName("registro/nuevo");
    }

    // —— Seguridad HTTP —— 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Para evitar problemas con formularios de login en desarrollo
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                // recursos públicos + estáticos
                .requestMatchers(
                        "/", "/index", "/errores/**",
                        "/registro/**", "/carrito/**", "/pruebas/**", "/reportes/**",
                        "/js/**", "/css/**", "/images/**", "/webjars/**"
                ).permitAll()
                // solo ADMIN puede crear/editar/eliminar
                .requestMatchers(
                        "/producto/nuevo", "/producto/guardar", "/producto/modificar/**", "/producto/eliminar/**",
                        "/categoria/nuevo", "/categoria/guardar", "/categoria/modificar/**", "/categoria/eliminar/**",
                        "/usuario/nuevo", "/usuario/guardar", "/usuario/modificar/**", "/usuario/eliminar/**",
                        "/reportes/**"
                ).hasRole("ADMIN")
                // ADMIN o VENDEDOR pueden ver listados de producto y categoría
                .requestMatchers(
                        "/producto/listado",
                        "/categoria/listado"
                ).hasAnyRole("ADMIN", "VENDEDOR")
                // **solo ADMIN** puede ver el listado de usuarios
                .requestMatchers("/usuario/listado")
                .hasRole("ADMIN")
                // USER puede facturar carrito
                .requestMatchers("/facturar/carrito")
                .hasRole("USER")
                // cualquier otra petición requiere autenticar
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

//    @Bean
//    public UserDetailsService users() {
//        UserDetails admin = User.builder()
//            .username("juan")
//            .password("{noop}123")
//            .roles("USER","VENDEDOR","ADMIN")
//            .build();
//        UserDetails sales = User.builder()
//            .username("rebeca")
//            .password("{noop}456")
//           .roles("USER","VENDEDOR")
//            .build();
//        UserDetails user = User.builder()
//            .username("pedro")
//            .password("{noop}789")
//            .roles("USER")
//            .build();
//        return new InMemoryUserDetailsManager(user, sales, admin);
//    }
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configurerGlobar(AuthenticationManagerBuilder build) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
