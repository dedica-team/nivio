/**
 * Returns the correct path prefixed with either REACT_APP_BACKEND_URL or the current location before the 'hash' route.
 *
 * @param subPath optional path to append
 */
export const withBasePath = (subPath: string | null) => {

    let backendUrl = process.env.REACT_APP_BACKEND_URL || `${window.location.href.split('#')[0]}`;
    if (!backendUrl.endsWith('/')) {
        backendUrl += '/';
    }
    if (subPath && subPath.startsWith('/')) {
        subPath = subPath.substr(1);
    }
    return `${backendUrl}${subPath}`;
};