import { FullyQualifiedIdentifier } from './FullyQualifiedIdentifier';

describe('FullyQualifiedIdentifier', () => {
  it('should parse an item fqi', () => {
    const fullyQualifiedIdentifier = new FullyQualifiedIdentifier('item://l1/u1/c1/g1/i1');

    expect(fullyQualifiedIdentifier.getLandscape()).toBe('l1');
    expect(fullyQualifiedIdentifier.getUnit()).toBe('u1');
    expect(fullyQualifiedIdentifier.getContext()).toBe('c1');
    expect(fullyQualifiedIdentifier.getGroup()).toBe('g1');
    expect(fullyQualifiedIdentifier.getItem()).toBe('i1');
  });

  it('should parse a context fqi', () => {
    const fullyQualifiedIdentifier = new FullyQualifiedIdentifier('context://l1/u1/c1');

    expect(fullyQualifiedIdentifier.getLandscape()).toBe('l1');
    expect(fullyQualifiedIdentifier.getUnit()).toBe('u1');
    expect(fullyQualifiedIdentifier.getContext()).toBe('c1');
    expect(fullyQualifiedIdentifier.getGroup()).toBe(null);
    expect(fullyQualifiedIdentifier.getItem()).toBe(null);
  });

  it('should throw', () => {
    expect(() => new FullyQualifiedIdentifier('context://')).toThrowError("Landscape identifier is empty")
  });
});
