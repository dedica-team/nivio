import React, { ReactElement, useState, useEffect } from 'react';
import Modal from 'react-modal';

interface IGenericModalProps {
  modalContent: string | ReactElement | ReactElement[] | null;
}

const GenericModal: React.FC<IGenericModalProps> = ({ modalContent }) => {
  Modal.setAppElement('#root');

  const [showModal, setShowModal] = useState(false);

  // We have to save modalContent in a state independent of our props to use shouldCloseOn functions from react-modal
  // If we just check our prop modalContent it wont be null until WE tell it to outside of our GenericModal
  // -> showModal would always be true
  const [modalContentState, setModalContentState] = useState(modalContent);

  useEffect(() => {
    if (modalContent) {
      setModalContentState(modalContent);
    } else {
      setModalContentState(null);
    }
  }, [modalContent]);

  useEffect(() => {
    if (modalContentState) {
      setShowModal(true);
    } else {
      setShowModal(false);
    }
  }, [modalContentState]);

  // Will only be used if we close it with ESC or clicking on overlay
  // If we want to close it with a button, we need to set modalContent in our parent component to null
  const closeModal = () => {
    setModalContentState(null);
  };

  return (
    <Modal
      isOpen={showModal}
      className='Modal'
      overlayClassName='Overlay'
      shouldCloseOnEsc={true}
      shouldCloseOnOverlayClick={true}
      onRequestClose={closeModal}
      contentLabel='Modal'
    >
      {modalContentState}
    </Modal>
  );
};

export default GenericModal;
