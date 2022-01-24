describe('Item test', () => {
  beforeEach(() => {
    cy.visit('http://localhost:3000');
  });
  it('Visits Nivio and checks the items', () => {
    cy.wait(1000);
    cy.get('[href^="#/landscape/"]').each(($el, index, $list) => {
      cy.wrap($el)
        .invoke('attr', 'href')
        .then((href) => {
          cy.visit(href);
          cy.url().should('include', href);
        });
      cy.get('.ReactSVGPanZoom > svg:nth-child(1)').click();
      cy.get('[style="position: absolute; right: 0.5em;"] > .MuiButtonBase-root').click();

      cy.get('[class="item unselected"]').each(($el, index, $list) => {
        cy.get('[class^="makeStyles-sideBar-"]').should('not.be.visible');
        cy.wrap($el).click();
        console.log($el.find('[class="label"]').text());
        cy.get('[class^="makeStyles-sideBar-"]').should('be.visible');
        cy.get('.MuiCardHeader-root').should('contain.text', $el.find('[class="label"]').text(), {
          matchCase: false,
        });
        cy.get('.MuiCardHeader-action > .MuiButtonBase-root').click();
      });
      cy.get('.MuiList-root > [tabindex="0"]')
        .invoke('attr', 'href')
        .then((href) => {
          cy.visit(href);
        });
      cy.wait(400);
    });
  });
});
