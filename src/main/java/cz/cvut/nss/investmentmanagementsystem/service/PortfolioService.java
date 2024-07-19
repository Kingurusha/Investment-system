package cz.cvut.nss.investmentmanagementsystem.service;

import cz.cvut.nss.investmentmanagementsystem.helper.validator.PortfolioValidator;
import cz.cvut.nss.investmentmanagementsystem.model.Asset;
import cz.cvut.nss.investmentmanagementsystem.model.Portfolio;
import cz.cvut.nss.investmentmanagementsystem.model.User;
import cz.cvut.nss.investmentmanagementsystem.model.enums.TransactionType;
import cz.cvut.nss.investmentmanagementsystem.repository.PortfolioRepository;
import cz.cvut.nss.investmentmanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class PortfolioService implements CrudService<Portfolio, Long>{
    private final PortfolioRepository portfolioRepository;
    private final PortfolioValidator portfolioValidator;
    private final UserRepository userRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, PortfolioValidator portfolioValidator, UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioValidator = portfolioValidator;
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public void create(Portfolio portfolio){
        portfolio.setTotalValue(BigDecimal.valueOf(0));
        User user = userRepository.findById(portfolio.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + portfolio.getUser().getId()));
        portfolio.setUser(user);
        portfolioRepository.save(portfolio);
    }
    @Override
    @Transactional(readOnly = true)
    public Portfolio get(Long portfolioId){
        portfolioValidator.validateExistById(portfolioId);
        return portfolioRepository.findById(portfolioId).get();
    }
    @Override
    @Transactional
    public void update(Portfolio portfolio){
        portfolioValidator.validateExistById(portfolio.getId());
        portfolioRepository.save(portfolio);
    }
    @Override
    @Transactional
    public void delete(Long portfolioId){
        portfolioValidator.validateExistById(portfolioId);
        portfolioRepository.deleteById(portfolioId);
    }
    @Transactional(readOnly = true)
    public Set<Asset> getAllAssetInPortfolio(Long portfolioId){
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found with ID: " + portfolioId));
        return portfolio.getAssets();
    }
    @Transactional
    public void rebalancePortfolio(Long portfolioId, Asset newAsset, TransactionType transactionType){
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found with ID: " + portfolioId));
        BigDecimal assetPrice = newAsset.getQuantity().multiply(newAsset.getMarketData().getCurrentPrice());
        if (transactionType == TransactionType.BUY){
            portfolio.setTotalValue(portfolio.getTotalValue().add(assetPrice));
        } else if(transactionType == TransactionType.SELL){
            portfolio.setTotalValue(portfolio.getTotalValue().subtract(assetPrice));
        } else {
            throw new IllegalArgumentException("Unsupported transaction type: " + transactionType);
        }
        portfolioRepository.save(portfolio);
    }
    @Transactional(readOnly = true)
    public List<Portfolio> getAllPortfoliosByUserIdOrderByTotalValueDesc(Long userId){
        return portfolioRepository.findAllByUserIdOrderByTotalValueDesc(userId);
    }
    @Transactional(readOnly = true)
    public List<Portfolio> getAllPortfoliosByUserIdOrderByTotalValueAsc(Long userId){
        return portfolioRepository.findAllByUserIdOrderByTotalValueAsc(userId);
    }
}