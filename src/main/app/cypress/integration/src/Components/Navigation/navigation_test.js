describe('Navigation test', () => {
  it('Visits Nivio and checks the navigation bar', () => {
    cy.visit('http://localhost:3000');
    cy.url().should('include', '/#/');
    cy.wait(400);

    cy.get('[href^="#/landscape/"]').each(($el, index, $list) => {
      cy.wrap($el).click();
      cy.url().should('include', '/landscape/');
      cy.get('.MuiAvatar-img').click();
      cy.get('.MuiList-root > [tabindex="0"]').click();
      cy.url().should('match', new RegExp('/#/$'));
      cy.wait(400);
      cy.get('.MuiAvatar-img').click();
      cy.get('[href="#/man/install.html"]').click();
      cy.url().should('include', '/#/man/install.html');
      cy.get('.MuiAvatar-img').click();
      cy.get('.MuiList-root > [tabindex="0"]').click();
    });
  });
});
