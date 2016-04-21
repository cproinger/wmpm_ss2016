package at.ac.tuwien.wmpm.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import at.ac.tuwien.wmpm.domain.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long>{

}
