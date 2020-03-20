import React, { ReactElement } from 'react';

interface IModalContext{
    modalContent: ReactElement | null,
}

const ModalContextDefaultValues = {
    modalContent: null,
};

const ModalContext = React.createContext<IModalContext>(ModalContextDefaultValues);

export default ModalContext;