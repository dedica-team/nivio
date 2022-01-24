describe('Navigation test', () => {
  beforeEach(() => {
    cy.visit('http://localhost:3000');
  });
  it('Visits Nivio and checks the navigation bar', () => {
    cy.url().should('include', '/#/');
    cy.wait(1000);
    cy.get('[href^="#/landscape/"]').first().click();
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

  it('Visits Nivio and checks if all landscapes are reachable', () => {
    cy.wait(1000);
    cy.get('[href^="#/landscape/"]').each(($el, index, $list) => {
      cy.wrap($el)
        .invoke('attr', 'href')
        .then((href) => {
          cy.visit(href);
          cy.url().should('include', href);
        });
      cy.get('.MuiList-root > [tabindex="0"]')
        .invoke('attr', 'href')
        .then((href) => {
          cy.visit(href);
          cy.url().should('match', new RegExp('/#/$'));
        });
      cy.wait(400);
    });
  });
});
