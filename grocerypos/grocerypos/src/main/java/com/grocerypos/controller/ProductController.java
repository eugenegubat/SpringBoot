package com.grocerypos.controller;

import com.grocerypos.model.Product;
import com.grocerypos.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Display product list and search results
    @GetMapping
    public String listProducts(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("products", productService.searchProducts(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("newProduct", new Product());
        return "products"; // Maps to products.html template
    }

    // Save a new product or update an existing one
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("newProduct") Product product, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes, 
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            return "products";
        }
        
        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product saved successfully!");
        return "redirect:/products";
    }

    // Delete a product by ID
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        return "redirect:/products";
    }
}
