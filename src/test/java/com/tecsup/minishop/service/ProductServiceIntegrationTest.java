package com.tecsup.minishop.service;

import com.tecsup.minishop.model.Product;
import com.tecsup.minishop.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    // @MockBean reemplaza el bean real en el contexto de Spring para simular la
    // base de datos
    @MockBean
    private ProductRepository productRepository;

    @Test
    @DisplayName("Debe guardar un producto válido correctamente")
    void shouldSaveValidProduct() {
        // Arrange (Preparar)
        Product input = Product.builder().name("Auriculares Sony").price(320.00).stock(15).build();
        Product expected = Product.builder().id(1L).name("Auriculares Sony").price(320.00).stock(15).build();

        when(productRepository.save(any(Product.class))).thenReturn(expected);

        // Act (Actuar)
        Product result = productService.save(input);

        // Assert (Verificar usando AssertJ en consistencia con tu código)
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Auriculares Sony ROTOS INTENCIONALMENTE");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el precio es cero o negativo")
    void shouldThrowExceptionWhenPriceIsInvalid() {
        // Arrange
        Product product = Product.builder().name("Producto inválido").price(0.0).stock(5).build();

        // Act & Assert
        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El precio debe ser mayor a cero");

        // Verifica que NUNCA se llamó al repositorio para guardar datos erróneos
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el stock es negativo")
    void shouldThrowExceptionWhenStockIsNegative() {
        // Arrange
        Product product = Product.builder().name("Producto sin stock").price(100.00).stock(-1).build();

        // Act & Assert
        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El stock no puede ser negativo");

    }

    @Test
    @DisplayName("Debe retornar todos los productos")
    void shouldReturnAllProducts() {
        // Arrange
        List<Product> mockProducts = List.of(
                Product.builder().id(1L).name("Producto A").price(100.0).stock(5).build(),
                Product.builder().id(2L).name("Producto B").price(200.0).stock(3).build());
        when(productRepository.findAll()).thenReturn(mockProducts);

        // Act
        List<Product> result = productService.findAll();

        // Assert aplicando el mismo estilo de extracción exhaustivo que implementaste
        assertThat(result).hasSize(2);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto no existe por ID")
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Producto no encontrado con id: 99");

    }
}