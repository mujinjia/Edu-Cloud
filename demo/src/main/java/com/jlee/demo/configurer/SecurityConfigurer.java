package com.jlee.demo.configurer;

/**
 * Spring Security 中 @EnableWebSecurity 注解用来加载 Spring Security 的配置
 * 引入 spring-boot-starter-security  会引入  spring-boot-starter，在 spring-boot-starter 中的 SecurityAutoConfiguration 类会加上 @EnableWebSecurity 注解，所以如果我们是使用了 starter 就可以不需要加该注解
 * <p>
 * 定义了 WebSecurityConfigurerAdapter 类，默认的一些配置也将会失效
 * <p>
 * 其中 @EnableGlobalMethodSecurity(prePostEnabled = true) 注解用于开启基于方法的安全认证机制，也就是说在 web 层的 controller 启用注解机制的安全确认，
 *
 * @author jlee
 * @date 2021/08/01
 */

//
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Order(50)
//public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http.authorizeRequests()
////                .antMatchers("/api/v1/account/token")
////                .permitAll()
////                .antMatchers("/api/**").authenticated()
////                .and()
////                .csrf().disable()
////                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
//
//        http.authorizeRequests()
//                .anyRequest()  // 匹配所有路径
//                .authenticated()   // 通过认证的用户才能访问
//                .and()
//                .formLogin()  // 配置表单登录页面
//                .and()
//                .httpBasic();   // 配置Http基础认证
//    }
//
////    @Override
////    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//////        auth.ldapAuthentication()
////    }
////
////    /**
////     * @param passwordEncoder 程序使用的加密方式
////     * @return InMemoryUserDetailsManager
////     */
////    @Bean
////    public InMemoryUserDetailsManager inMemoryUserDetailsManager(
////            ObjectProvider<PasswordEncoder> passwordEncoder) {
////
////        final PasswordEncoder encoder = passwordEncoder.getIfAvailable();
////
////        String password = "haha";
////        if (encoder != null) {
////            password = encoder.encode("haha");
////        } else {
////            // 如果密码是明文需要给密码加{noop}前缀
////            password = "{noop}" + password;
////        }
////
////        return new InMemoryUserDetailsManager(
////                User.withUsername("haha").password(password)
////                        .roles("ADMIN").build());
////    }
//}