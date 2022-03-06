export class FullyQualifiedIdentifier {
  private readonly type: string;
  private readonly landscape: string;
  private readonly unit: string | null;
  private readonly context: string | null;
  private readonly group: string | null;
  private readonly item: string | null;

  constructor(fqi: string) {
    let path = fqi.split('://')[1];
    this.type = path[0];
    let parts = path.split('/');
    if (parts[0].length === 0) {
      throw new Error('Landscape identifier is empty');
    }
    this.landscape = parts[0];
    this.unit = parts.length > 1 ? parts[1] : null;
    this.context = parts.length > 2 ? parts[2] : null;
    this.group = parts.length > 3 ? parts[3] : null;
    this.item = parts.length > 4 ? parts[4] : null;
  }

  /**
   * Returns the identifier of the landscape
   */
  getLandscape(): string {
    return this.landscape;
  }

  /**
   * Returns the identifier of the unit (if applicable)
   */
  getUnit(): string | null {
    return this.unit;
  }

  /**
   * Returns the identifier of the context (if applicable)
   */
  getContext(): string | null {
    return this.context;
  }

  /**
   * Returns the identifier of the group (if applicable)
   */
  getGroup(): string | null {
    return this.group;
  }

  /**
   * Returns the identifier of the item (if applicable)
   */
  getItem(): string | null {
    return this.item;
  }
}
