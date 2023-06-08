function phoneEdit(phone) {
    phone = phone.replace(/\[/gm, '');
    phone = phone.replace(/"/gm, '');
    phone = phone.replace(/]/gm, '');
    phone = phone.replace(/b'/gm, '');
    phone = phone.replace(/'/gm, '');
    return phone
};